(ns events.core
  (:gen-class)
  (:require [clj-http.client :as client])
  (:require [clojure.string :as str])
  (:require [events.event :refer [newEvent eventDateValue]])
  (:require [ring.middleware.params :refer [wrap-params]])
  (:require [ring.util.response :refer [response content-type]])
  (:require [ring.adapter.jetty :refer [run-jetty]])
  (:require [ring.middleware.json :refer [wrap-json-response]])
  (:require [redis.core :refer [getValue setValue]])
  (:require [environ.core :refer [env]]))

;; (def calUrl "http://localhost:8000/finland.ics")
(defn getCalUrl
  "Returns propert ical api url for given country code."
  [country]
  (if (= "fi" country)
    "https://www.officeholidays.com/ics-clean/finland"
    "https://www.officeholidays.com/ics-clean/sweden"))

(defn createKey
  "Creates event data key"
  [key]
  (-> key
      (str/lower-case)
      (str/split #";" 2)
      (first)))

(defn createEventRow
  "Creates event data from event string stump"
  [row year]
  (let [rowData (into {} (->> (clojure.string/split-lines row)
                              (map #(let [[key val] (str/split % #":" 2)]
                                      {(createKey key) val}))))
        date (eventDateValue rowData)]
    (if (and (not-empty date) (or (empty? year) (= year (subs date 0 4)))) (newEvent rowData) nil)))

(defn parseEventsResponse
  "Parses ics to more usable format"
  [ics year]
  (let [rowString (->> (str/split ics #"\s*BEGIN:VEVENT\s*")
                       (map #(str/replace %1 #"END:VEVENT\s*" "")))]
    (filter not-empty (map #(createEventRow % year) rowString))))

(defn loadCalendarEvents
  "Loads calendar data from the server"
  [country]
  (let [cacheKey (str "ics-response-" country), cache (getValue cacheKey)]
    (if cache cache (let [res (:body (client/get (getCalUrl country) {:insecure? true}))]
                      (setValue cacheKey res)
                      res))))

(defn filterEvents
  "Filters out events that are not usable for our purposes (weekends, maybe some other)"
  [events]
  (remove #(or (= (get % :day) "Sat") (= (get % :day) "Sun")) events))

(defn getEvents
  "Gets holiday events for given year and country"
  [year country]
  (let [y (str year)]
    (filterEvents (-> (loadCalendarEvents country)
                      (parseEventsResponse y)))))


(defn mergeEventLists
  "Merges two list of events into one event"
  [list1 list2]
  (->> (concat list1 list2)
       (sort-by :date)
       (partition-by :date)
       (map (partial
             apply
             merge-with (fn [x y] (if (= x y) x (list x y)))))))

(defn handler [{{year "year" country "country"} :params}]
  (let [yearVal (or year "")]
    (-> (if (empty? country)
          (mergeEventLists (getEvents yearVal "fi") (getEvents yearVal "se"))
          (getEvents yearVal country))
        response
        (content-type "json"))))

(def app
  (->
   handler
   wrap-params
   wrap-json-response))

(defn -main []
  (run-jetty app {:port (or (env :port) 3388)}))

(when (= "dev" (env :environment)) (-main))
