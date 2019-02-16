(ns pedestal-datomic-todo-api.controllers.todos
  (:require [pedestal-datomic-todo-api.db.todos :as db]))

(defn create-todo! [storage text]
  (db/create-todo! storage text))

(defn get-todo [storage id]
  (db/get-todo storage id))

(defn get-todos [storage]
  (db/get-todos storage))

(defn update-todo! [storage id text done]
  (db/update-todo! storage id text done))

(defn delete-todo! [storage id]
  (db/delete-todo! storage id))
