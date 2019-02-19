(ns pedestal-datomic-todo-api.adapters)

(defn str->uuid [id-str]
  (read-string (str "#uuid \"" id-str "\"")))

(defn str->bool [bool-str]
  (cond
    (or (= bool-str "true") (= bool-str "1")) true
    :else false))

(defn todo-datomic->json [db-todo]
  (let [todo (first db-todo)]
    (-> (merge {} (if-some [id (:todo/id todo)] {:id id} nil))
        (merge (if-some [text (:todo/text todo)] {:text text} nil))
        (merge (if-some [done (:todo/done? todo)] {:done done} nil)))))
