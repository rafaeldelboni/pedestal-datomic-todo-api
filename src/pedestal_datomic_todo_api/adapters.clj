(ns pedestal-datomic-todo-api.adapters)

(defn str->uuid [id-str]
  (read-string (str "#uuid \"" id-str "\"")))

(defn str->bool [bool-str]
  (read-string bool-str))

(defn todos-datomic->json [todos]
  (let [todo (first todos)]
    (-> (merge {} (if-let [id (:todo/id todo)] {:id id} nil))
        (merge (if-let [text (:todo/text todo)] {:text text} nil))
        (merge (if-let [done (some? (:todo/done? todo))] {:done done} nil)))))
