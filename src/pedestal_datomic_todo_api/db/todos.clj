(ns pedestal-datomic-todo-api.db.todos
  (:require [datomic.api :as d]))

(def ^:private db-get-todos 
  '[:find (pull ?e [:todo/id :todo/done? :todo/text])
    :where [?e :todo/id ?id]
           [?e :todo/done? ?done]
           [?e :todo/text ?text]])

(def ^:private db-get-todo
  '[:find (pull ?e [:todo/id :todo/done? :todo/text])
    :in $ ?id
    :where [?e :todo/id ?id]])

(defn- db-build-todo
  [tempid id text done?]
  {:db/id      tempid
    :todo/id    id
    :todo/text  text
    :todo/done? done?})

(defn- db-new-todo
  [tempid text]
  (db-build-todo tempid (d/squuid) text false))

(defn- db-delete-todo
  [id]
  [[:db.fn/retractEntity [:todo/id id]]])

(defn create-todo!
  [conn text]
  (let [todo (db-new-todo (d/tempid :db.part/user) text)]
    @(d/transact conn [todo])
    todo))

(defn get-todo
  [conn id]
  (get-in (d/q db-get-todo (d/db conn) id) [0 0]))

(defn get-todos
  [conn]
  (d/q db-get-todos (d/db conn)))

(defn update-todo!
  [conn id text done]
  (let [todo
        (db-build-todo
          (d/tempid :db.part/user)
          (read-string (str "#uuid \"" id "\""))
          text
          done)]
    @(d/transact conn [todo])
    todo))

(defn delete-todo!
  [conn id]
  (let [uuid (read-string (str "#uuid \"" id "\""))
        todo (db-delete-todo uuid)]
    @(d/transact conn todo)
    uuid))
