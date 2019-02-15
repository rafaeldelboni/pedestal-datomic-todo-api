(ns pedestal-datomic-todo-api.components.storage-datomic
  (:require [pedestal-datomic-todo-api.protocols.storage-client :as storage-client]
            [com.stuartsierra.component :as component]
            [datomic.api :as d]))

(def ^:private schema
  [{:db/doc         "todo unique id"
    :db/ident       :todo/id
    :db/index       true
    :db/valueType   :db.type/uuid
    :db/unique      :db.unique/identity
    :db/cardinality :db.cardinality/one
    :db/id          (d/tempid :db.part/db)}
   {:db/doc         "todo done flag"
    :db/ident       :todo/done?
    :db/index       true
    :db/valueType   :db.type/boolean
    :db/cardinality :db.cardinality/one
    :db/id          (d/tempid :db.part/db)}
   {:db/doc         "todo description"
    :db/ident       :todo/text
    :db/index       true
    :db/fulltext    true
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/id          (d/tempid :db.part/db)}])

(defn init-connect-to-database [db-uri]
  (d/create-database db-uri)
  (let [conn (d/connect db-uri)]
    @(d/transact conn schema)
    conn))

(defrecord StorageDatomic [uri connection]
  component/Lifecycle

  (start [component]
    (println ";; Starting database")
    (let [conn (init-connect-to-database uri)]
      (assoc component :connection conn)))

  (stop [component]
    (println ";; Stopping database")
    (assoc component :connection nil))

  storage-client/StorageClient
  (query [_this data args] (d/q data (d/db connection) args))
  (exec! [_this data] @(d/transact connection data)))

(defn new-storage-datomic [uri]
  (map->StorageDatomic {:uri uri}))
