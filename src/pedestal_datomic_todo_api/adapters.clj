(ns pedestal-datomic-todo-api.adapters)

(defn str->uuid [id-str]
  (read-string (str "#uuid \"" id-str "\"")))
