(ns pedestal-datomic-todo-api.components.storage-datomic
  (:require [clojure.java.io :as io]
            [com.stuartsierra.component :as component]
            [datomic.api :as d]
            [pedestal-datomic-todo-api.protocols.storage-client :as storage-client]))

(def ^:private schema
  (read-string (slurp (io/resource "schema.edn"))))

(defn init-connect-to-database [db-uri]
  (d/create-database db-uri)
  (let [conn (d/connect db-uri)]
    @(d/transact conn schema)
    conn))

(defrecord StorageDatomic [config connection]
  component/Lifecycle

  (start [component]
    (println ";; Starting database")
    (let [conn (init-connect-to-database (get-in config [:config :db-uri]))]
      (assoc component :connection conn)))

  (stop [component]
    (println ";; Stopping database")
    (assoc component :connection nil))

  storage-client/StorageClient
  (query [_this data args] (d/q data (d/db connection) args))
  (exec! [_this data] @(d/transact connection data)))

(defn new-storage-datomic [] (map->StorageDatomic {}))
