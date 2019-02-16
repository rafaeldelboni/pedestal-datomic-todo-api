(ns pedestal-datomic-todo-api.core
  (:require [com.stuartsierra.component :as component]
            [pedestal-datomic-todo-api.components.config :as config]
            [pedestal-datomic-todo-api.components.routes :as routes]
            [pedestal-datomic-todo-api.components.storage-datomic :as storage]
            [pedestal-datomic-todo-api.components.webserver :as webserver]
            [pedestal-datomic-todo-api.server :as server]
            [pedestal-datomic-todo-api.service :as service]))

(def config-map 
  {:db-uri "datomic:free://localhost:4334/todos"
   :http-port 8080})

(def system (atom nil))

(defn- build-system-map []
  (component/system-map
    :config (config/new-config config-map)
    :storage (component/using (storage/new-storage-datomic) [:config])
    :routes  (routes/new-routes #'pedestal-datomic-todo-api.service/routes)
    :http-server (component/using (webserver/new-webserver) [:config :routes :storage])))

(defn -main
  "The entry-point for 'lein run-dev'"
  [& args]
  (-> (build-system-map)
      (server/start-system! system)))
