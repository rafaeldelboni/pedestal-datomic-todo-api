(ns pedestal-datomic-todo-api.controllers.todos
  (:require [dawcs.flow :refer [call then else else-if]]
            [pedestal-datomic-todo-api.adapters :as ad]
            [pedestal-datomic-todo-api.db.todos :as db]
            [pedestal-datomic-todo-api.schemes.todos :as sch]))

(defn check-todo-exists [storage id]
  (println (:todo/id (db/get-todo storage id)))
  (or (db/get-todo storage id)
      (ex-info "Todo not found" {:status 404})))

(defn create-todo! [storage todo]
  (->> todo
       (then #(ad/coerce sch/NewTodo %))
       (else-if Exception (partial ad/format-throw 400))
       (then #(db/create-todo! storage %))
       (then #(ad/todo-datomic->json %))
       (then #(ad/format-result 201 %))
       (else ad/format-error)))

(defn get-todo [storage id]
  (->> id
       (then #(ad/str->uuid %))
       (else-if Exception (partial ad/format-throw 400))
       (then #(check-todo-exists storage %))
       (then #(ad/todo-datomic->json %))
       (then #(ad/format-result 200 %))
       (else ad/format-error)))

(defn get-todos [storage]
  (->> storage
       (then #(db/get-todos %))
       (then #(map ad/todo-datomic->json %))
       (then #(ad/format-result 200 %))
       (else ad/format-error)))

(defn update-todo! [storage id todo]
  (->> id
       (then #(ad/str->uuid %))
       (else-if Exception (partial ad/format-throw 400))
       (then #(check-todo-exists storage %))
       (then #(assoc todo :id (:todo/id %)))
       (then #(ad/coerce sch/Todo %))
       (else-if Exception (partial ad/format-throw 400))
       (then #(db/update-todo! storage %))
       (then #(ad/todo-datomic->json %))
       (then #(ad/format-result 200 %))
       (else ad/format-error)))

(defn delete-todo! [storage id]
  (->> id
       (then #(ad/str->uuid %))
       (else-if Exception (partial ad/format-throw 400))
       (then #(check-todo-exists storage %))
       (then #(db/delete-todo! storage (:todo/id %)))
       (then #(ad/todo-datomic->json %))
       (then #(ad/format-result 200 %))
       (else ad/format-error)))
