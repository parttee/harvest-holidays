(ns events.core
  (:require [clj-http.client :as client])
  (:require [clojure.string :as str]))

(def calUrl "http://localhost:8000/finland.ics")
;; (def calUrl "https://www.officeholidays.com/ics/finland")

(defstruct .Holiday :day :holiday :date)

(defn loadEventContent
  "Loads the event table from the website"
  []
  (:body (client/get calUrl {:insecure? true})))

(defn parseEventRow [row year]
  (let [rowData (into {} (->> (clojure.string/split-lines row)
                              (map #(let [[key val] (str/split % #":")]
                                      {(str/lower-case key) val}))))
        date (get rowData "dtstamp")]
    (if (and (not-empty date) (= year (subs date 0 4))) rowData nil)))

(defn parseEvents
  "Parse ics to more usable format"
  [ics year]
  (let [rowString (->> (str/split ics #"\s*BEGIN:VEVENT\s*")
                       (map #(str/replace %1 #"END:VEVENT\s*" "")))]
    (filter not-empty (map #(parseEventRow % year) rowString))))

(defn getEvents
  "Gets holiday events for given year"
  [year]
  (let [y (str year)]
    (-> (loadEventContent)
        (parseEvents y))))

(println (getEvents 2021))
