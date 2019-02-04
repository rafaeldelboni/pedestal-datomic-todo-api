(ns pedestal-datomic-todo-api.core
  (:require [datomic.api :as d]
            [io.pedestal.http :as http]))

(def schema
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

(defn init-db! []
  (d/create-database db-uri)
  (let [conn (d/connect db-uri)]
    @(d/transact conn schema)
    conn))

(def query-all-todos '[:find ?e ?id ?done ?text
                       :where [?e :todo/id ?id]
                              [?e :todo/done? ?done]
                              [?e :todo/text ?text]])

(defn query-create-todo
  [tempid text]
  [{:db/id tempid
    :todo/id   (d/squuid)
    :todo/done? false
    :todo/text text}])

;@(d/transact conn (query-create-todo (d/tempid :db.part/user) "learn clj"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
