(ns pedestal-datomic-todo-api.db.todos
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

(def ^:private db-uri "datomic:free://localhost:4334/todos")


(def ^:private query-todos '[:find ?e ?id ?done ?text
                       :where [?e :todo/id ?id]
                              [?e :todo/done? ?done]
                              [?e :todo/text ?text]])

(defn- db-build-todo
  [tempid id text done?]
  [{:db/id      tempid
    :todo/id    id
    :todo/text  text
    :todo/done? done?}])

(defn- db-new-todo
  [tempid text]
  (db-build-todo tempid (d/squuid) text false))

(defn- db-delete-todo
  [id]
  [[:db.fn/retractEntity [:todo/id id]]])

(defn init-db-conn! []
  (d/create-database db-uri)
  (let [conn (d/connect db-uri)]
    @(d/transact conn schema)
    conn))

(defn create-todo!
  [conn text]
  (let [todo (db-new-todo (d/tempid :db.part/user) text)]
    @(d/transact conn todo)
    todo))

(defn read-todos
  [conn]
  (d/q query-todos (d/db conn)))

(defn update-todo!
  [conn id text done]
  (let [todo
        (db-build-todo
          (d/tempid :db.part/user)
          (read-string (str "#uuid \"" id "\""))
          text
          done)]
    @(d/transact conn todo)
    todo))

(defn delete-todo!
  [conn id]
  (let [todo (db-delete-todo (read-string (str "#uuid \"" id "\"")))]
    @(d/transact conn todo)
    [:todo/id id]))
