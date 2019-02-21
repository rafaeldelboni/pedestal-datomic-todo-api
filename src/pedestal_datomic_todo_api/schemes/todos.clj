(ns pedestal-datomic-todo-api.schemes.todos
  (:require [schema.core :as s]))

(def NewTodo {:text s/Str})

(def Todo {:id s/Uuid
           :text s/Str
           :done s/Bool})
