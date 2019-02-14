(ns pedestal-datomic-todo-api.core
  (:require [io.pedestal.http :as http]
            [com.stuartsierra.component :as component]
            [pedestal-datomic-todo-api.service :as service]
            [pedestal-datomic-todo-api.components.storage-datomic :as storage]
            [pedestal-datomic-todo-api.components.routes :as routes]
            [pedestal-datomic-todo-api.components.webserver :as webserver]))

(def service
  {:env          :dev
   ::http/type   :jetty
   ::http/routes service/routes
   ::http/join?  false
   ::http/port 8080})

; TODO: replace main with build-and-start
(defn -main
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your server on port 8080")
  (->> service
       http/default-interceptors
       http/dev-interceptors
       http/create-server
       http/start))

(defn- build []
  (component/system-map
    :storage (storage/new-in-memory)
    :routes  (routes/new-routes #'pedestal-datomic-todo-api.service/routes)
    :server  (component/using (webserver/new-webserver) [:routes :storage])))

(defn build-and-start
  "Configure components dependencies and start the system"
  []
  (-> (build)
      (component/start)))
