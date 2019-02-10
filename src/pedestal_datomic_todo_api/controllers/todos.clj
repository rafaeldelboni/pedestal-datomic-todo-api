(ns pedestal-datomic-todo-api.controllers.todos
  (:require [pedestal-datomic-todo-api.db.todos :as db]))

(defn create-todo! [conn text]
  (db/create-todo! conn text))

(defn get-todo [conn id]
  (db/get-todo conn id))

(defn get-todos [conn]
  (db/get-todos conn))

(defn update-todo! [conn id text done]
  (db/update-todo! conn id text done))

(defn delete-todo! [conn id]
  (db/delete-todo! conn id))
