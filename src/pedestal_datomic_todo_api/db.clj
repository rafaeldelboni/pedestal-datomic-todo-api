(ns pedestal-datomic-todo-api.db
  (:require [datomic.api :as d]))

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

(def db-uri "datomic:free://localhost:4334/todos")

(defn init-db-conn! []
  (d/create-database db-uri)
  (let [conn (d/connect db-uri)]
    @(d/transact conn schema)
    conn))
