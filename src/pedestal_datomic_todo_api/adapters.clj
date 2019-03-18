(ns pedestal-datomic-todo-api.adapters
  (:require [schema.coerce :as coerce]))

(defn str->uuid [id-str]
  (read-string (str "#uuid \"" id-str "\"")))

(defn todo-datomic->json [todo]
    (-> (merge {} (if-some [id (:todo/id todo)] {:id id} nil))
        (merge (if-some [text (:todo/text todo)] {:text text} nil))
        (merge (if-some [done (:todo/done? todo)] {:done done} nil))))

(defn format-result [status result]
  {:result result :status status})

(defn format-throw [error-status error]
  (ex-info (ex-message error) {:status error-status}))

(defn format-error [^Throwable err]
  (assoc (ex-data err)
         :error (ex-message err)))

(defn coerce [schema body]
  ((coerce/coercer! schema coerce/json-coercion-matcher) body))
