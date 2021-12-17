(ns redis.core
  (:require [taoensso.carmine :as car :refer (wcar)])
  (:require [environ.core :refer [env]]))

(def server1-conn {:pool {} :spec {:uri (env :database-uri)}})

(defmacro wcar* [& body] `(wcar server1-conn ~@body))

(defn setValue [key value]
  (wcar* (car/set key value)))


(defn getValue [key]
  (wcar* (car/get key)))