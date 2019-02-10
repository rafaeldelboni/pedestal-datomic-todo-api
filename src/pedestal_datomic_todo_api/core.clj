(ns pedestal-datomic-todo-api.core
  (:require [io.pedestal.http :as http]
            [pedestal-datomic-todo-api.service :as service]))

(def service
  {:env          :dev
   ::http/type   :jetty
   ::http/routes service/routes
   ::http/join?  false
   ::http/port 8080})

(defn -main
  "The entry-point for 'lein run-dev'"
  [& args]
  (->> service
       http/default-interceptors
       http/dev-interceptors
       http/create-server
       http/start))
