(ns events.core
  (:require [clj-http.client :as client])
  (:require [clojure.string :as str])
  (:require [events.event :refer [newEvent eventDateValue]]))

(def calUrl "http://localhost:8000/finland.ics")
;; (def calUrl "https://www.officeholidays.com/ics/finland")

(defn loadCalendarEvents
  "Loads calendar data from the server"
  []
  (:body (client/get calUrl {:insecure? true})))

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
    (if (and (not-empty date) (= year (subs date 0 4))) (newEvent rowData) nil)))

(defn parseEventsResponse
  "Parses ics to more usable format"
  [ics year]
  (let [rowString (->> (str/split ics #"\s*BEGIN:VEVENT\s*")
                       (map #(str/replace %1 #"END:VEVENT\s*" "")))]
    (filter not-empty (map #(createEventRow % year) rowString))))

(defn getEvents
  "Gets holiday events for given year"
  [year]
  (let [y (str year)]
    (-> (loadCalendarEvents)
        (parseEventsResponse y))))

(println (getEvents 2021))
