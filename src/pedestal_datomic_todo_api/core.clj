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

(def query-todos '[:find ?e ?id ?done ?text
                       :where [?e :todo/id ?id]
                              [?e :todo/done? ?done]
                              [?e :todo/text ?text]])

(defn db-build-todo
  [tempid id text done?]
  [{:db/id      tempid
    :todo/id    id
    :todo/text  text
    :todo/done? done?}])

(defn db-new-todo
  [tempid text]
  (db-build-todo tempid (d/squuid) text false))

(defn db-delete-todo
  [id]
  [[:db.fn/retractEntity [:todo/id id]]])

(defn create-todo
  [conn text]
  (let [todo (db-new-todo (d/tempid :db.part/user) text)]
    @(d/transact conn todo)
    todo))

;create
;@(d/transact conn (db-new-todo (d/tempid :db.part/user) "learn clj"))
;read
;(d/q query-all-todos (d/db conn))
;update
;@(d/transact conn (db-build-todo (d/tempid :db.part/user) #uuid "5c58c42a-1764-40bf-9c9b-49683a87d9f0" "changeme dude!!!!" true))
;delete
;@(d/transact conn (db-delete-todo #uuid "5c58c42a-1764-40bf-9c9b-49683a87d9f0"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
