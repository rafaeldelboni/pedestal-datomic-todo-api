(ns pedestal-datomic-todo-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [pedestal-datomic-todo-api.adapters :as adapters]
            [pedestal-datomic-todo-api.controllers.todos :as ctrl-todos]
            [pedestal-datomic-todo-api.schemes.todos :as sch-todos]
            [schema.core :as s]
            [schema.coerce :as coerce]
            [ring.util.response :as ring-resp]))

(defn- parse-json-request [schema body]
  ((coerce/coercer schema coerce/json-coercion-matcher) body))

(defn- bad-response [errors]
  (-> {:errors errors}
    ring-resp/response
    (ring-resp/status 500)))

(defn home-page
  [request]
  (ring-resp/response {:message "Hello World!!"}))

(defn create-todo
  [{body :json-params {storage :storage} :components}]
  (if-let [errors (s/check sch-todos/NewTodo body)]
    (bad-response errors)
    (ring-resp/response
      (->> (ctrl-todos/create-todo! storage body)
           (adapters/todo-datomic->json)))))

(defn get-todo
  [{{id :id} :path-params {storage :storage} :components}]
  (ring-resp/response
    (->> (ctrl-todos/get-todo storage (adapters/str->uuid id))
         (map adapters/todo-datomic->json)
         (first))))

(defn get-todos
  [{{storage :storage} :components}]
  (ring-resp/response
    (->> (ctrl-todos/get-todos storage)
         (map adapters/todo-datomic->json))))

(defn update-todo
  [{body :json-params {storage :storage} :components}]
  (let [{errors :error, :as todo} (parse-json-request sch-todos/Todo body)]
    (if (some? errors)
      (bad-response errors)
      (ring-resp/response
        (->> (ctrl-todos/update-todo! storage todo)
             (adapters/todo-datomic->json))))))

(defn delete-todo
  [{{id :id} :path-params {storage :storage} :components}]
  (ring-resp/response
    (->> (ctrl-todos/delete-todo! storage (adapters/str->uuid id))
         (adapters/todo-datomic->json))))

(def common-interceptors
  [(body-params/body-params) http/json-body])

(def routes
  #{["/" :get (conj common-interceptors `home-page)]
    ["/todo/:id" :get (conj common-interceptors `get-todo)]
    ["/todo" :get (conj common-interceptors `get-todos)]
    ["/todo" :post (conj common-interceptors `create-todo)]
    ["/todo" :put (conj common-interceptors `update-todo)]
    ["/todo/:id" :delete (conj common-interceptors `delete-todo)]})
