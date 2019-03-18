(ns pedestal-datomic-todo-api.components.config
  (:require [com.stuartsierra.component :as component]))

(defrecord Config [config]
  component/Lifecycle
  (start [this] this)
  (stop  [this] this))

(def config-map
  {:db-uri (or (System/getenv "DB_URI") "datomic:free://localhost:4334/todos?password=my-pwd")
   :http-port (Integer/parseInt (or (System/getenv "HTTP_PORT") "8080"))
   :http-host (or (System/getenv "HTTP_HOST") "localhost")})

(defn new-config [input-map] (map->Config {:config (or input-map config-map)}))
