(ns pedestal-datomic-todo-api.controllers.todos
  (:require [pedestal-datomic-todo-api.db.todos :as db]))

(defn create-todo! [storage todo]
  (db/create-todo! storage todo))

(defn get-todo [storage id]
  (db/get-todo storage id))

(defn get-todos [storage]
  (db/get-todos storage))

(defn update-todo! [storage todo]
  (db/update-todo! storage todo))

(defn delete-todo! [storage id]
  (db/delete-todo! storage id))
