(ns pedestal-datomic-todo-api.adapters)

(defn str->uuid [id-str]
  (read-string (str "#uuid \"" id-str "\"")))

(defn str->bool [bool-str]
  (read-string bool-str))

(defn todo-datomic->json [todo]
  {:id (:todo/id todo)})
