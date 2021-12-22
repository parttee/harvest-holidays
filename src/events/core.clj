(ns events.core
  (:gen-class)
  (:require [clj-http.client :as client])
  (:require [clojure.string :as str])
  (:require [events.event :refer [newEvent eventDateValue]])
  (:require [ring.middleware.params :refer [wrap-params]])
  (:require [ring.util.response :refer [response content-type]])
  (:require [ring.adapter.jetty :refer [run-jetty]])
  (:require [ring.middleware.json :refer [wrap-json-response]])
  (:require [redis.core :refer [getValue setValue]]))

;; (def calUrl "http://localhost:8000/finland.ics")
(def calUrl "https://www.officeholidays.com/ics/finland")

(defn createKey
  "Creates event data key"
  [key]
  (-> key
      (str/lower-case)
      (str/split #";" 2)
      (first)))

(defn createValue
  "Creates event data value" [val] (str/replace val #"Finland: " ""))

(defn createEventRow
  "Creates event data from event string stump"
  [row year]
  (let [rowData (into {} (->> (clojure.string/split-lines row)
                              (map #(let [[key val] (str/split % #":" 2)]
                                      {(createKey key) (createValue val)}))))
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
  []
  (let [cache (getValue "ics-response")]
    (if cache cache (let [res (:body (client/get calUrl {:insecure? true}))]
                      (setValue "ics-response" res)
                      res))))

(defn filterEvents
  "Filters out events that are not usable for our purposes (weekends, maybe some other)"
  [events]
  (remove #(or (= (get % :day) "Sat") (= (get % :day) "Sun")) events))

(defn getEvents
  "Gets holiday events for given year"
  [year]
  (let [y (str year)]
    (filterEvents (-> (loadCalendarEvents)
                      (parseEventsResponse y)))))

(defn handler [{{year "year"} :params}]
  (-> (response (getEvents (or year "")))
      (content-type "json")))

(def app
  (->
   handler
   wrap-params
   wrap-json-response))

(defn -main []
  (run-jetty app {:port 3000}))
