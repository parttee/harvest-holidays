(defproject holidays "1.0.0-SNAPSHOT"
  :description "Save holidays to Harvest"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [clj-http/clj-http "3.12.0"]
                 [ring/ring-core "1.8.2"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-jetty-adapter "1.8.2"]]
  :main events.core
  ;; :target-path "server/%s"
  :profiles {:uberjar {:aot :all}})
