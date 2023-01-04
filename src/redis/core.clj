(ns redis.core
  (:require [taoensso.carmine :as car :refer (wcar)])
  (:require [environ.core :refer [env]]))

(def server1-conn {:pool :none :spec {:host (env :redis-host)
                                      :port (Integer/parseInt (or (env :redis-port) "6379"))
                                      :password (env :redis-pass)}})

(defmacro wcar* [& body] `(wcar server1-conn ~@body))

(defn setValue [key value]
  (wcar* (car/set key value))
  (wcar* (car/expire key (* 60 60 24 60))))


(defn getValue [key]
  (wcar* (car/get key)))