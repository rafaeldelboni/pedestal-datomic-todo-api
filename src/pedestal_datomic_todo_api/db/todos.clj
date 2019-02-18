(ns pedestal-datomic-todo-api.db.todos
  (:require [datomic.api :as d]
            [pedestal-datomic-todo-api.protocols.storage-client :as storage-cli]))

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
  [storage text]
  (let [todo (db-new-todo (d/tempid :db.part/user) text)]
    (storage-cli/exec! storage [todo])
    [todo]))

(defn get-todo
  [storage id]
  (storage-cli/query storage db-get-todo id))

(defn get-todos
  [storage]
  (storage-cli/query storage db-get-todos nil))

(defn update-todo!
  [storage id text done]
  (let [todo
        (db-build-todo
          (d/tempid :db.part/user)
          id
          text
          done)]
    (storage-cli/exec! storage [todo])
    [todo]))

(defn delete-todo!
  [storage id]
  (let [uuid id
        todo (db-delete-todo uuid)]
    (storage-cli/exec! storage todo)
    [{:todo/id uuid}]))
