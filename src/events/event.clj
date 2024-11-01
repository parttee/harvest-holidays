(ns events.event)

(defstruct .Holiday :day :holiday :date :country)

(defn eventDateValue
  "Returns event date value from given data"
  [event]
  (get event "dtstart"))

(defn eventNameValue
  "Returns event name value from given data"
  [event]
  (get event "summary"))

(defn newEvent
  "Returns a struct from given event data"
  [event]
  (let [sdfFrom (java.text.SimpleDateFormat. "yyyyMMdd")
        fromDate (.parse sdfFrom (eventDateValue event))
        sdfDate (java.text.SimpleDateFormat. "yyyy-MM-dd")
        sdfDay (java.text.SimpleDateFormat. "E")]

    (struct .Holiday
            (.format sdfDay fromDate)
            (eventNameValue event)
            (.format sdfDate fromDate)
            (get event "location"))))
