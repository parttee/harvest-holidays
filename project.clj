(defproject holidays "1.0.0-SNAPSHOT"
  :description "Save holidays to Harvest"
  :dependencies [[org.clojure/clojure "1.11.2"]
                 [clj-http/clj-http "3.13.0"]
                 [ring/ring-core "1.13.0"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-jetty-adapter "1.13.0"]
                 [com.taoensso/carmine "3.4.1"]
                 [environ "1.2.0"]
                 [org.slf4j/slf4j-api "2.0.16"]
                 [org.slf4j/slf4j-simple "2.0.16"]]
  :plugins [[lein-environ "1.2.0"]]
  :main events.core
  :profiles {:uberjar [:prod {:aot :all}]
             :dev [:project/dev :profiles/dev]
             :prod [:project/prod :profiles/prod]
           ;; only edit :profiles/* in profiles.clj
             :project/dev  {}
             :project/prod  {}})