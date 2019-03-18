(ns pedestal-datomic-todo-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [pedestal-datomic-todo-api.adapters :as adapters]
            [pedestal-datomic-todo-api.controllers.todos :as ctrl-todos]
            [pedestal-datomic-todo-api.schemes.todos :as sch-todos]
            [schema.coerce :as coerce]
            [ring.util.response :as ring-resp]))

(defn- parse-json-request [schema body]
  ((coerce/coercer schema coerce/json-coercion-matcher) body))

(defn- ok-response
  [result status]
   (-> result
       ring-resp/response
       (ring-resp/status status)))

(defn- bad-response
  ([errors]
   (bad-response errors 500))
  ([errors status]
   (let [st (if (nil? status) 500 status)]
     (-> {:errors errors}
         ring-resp/response
         (ring-resp/status st)))))

(defn- handle-response!
  [response]
  (let [{result :result errors :error status :status} response]
    (if (some? errors)
      (bad-response errors status)
      (ok-response result status))))

(defn home-page
  [request]
  (ring-resp/response {:message "Hello World!!"}))

(defn create-todo
  [{body :json-params {storage :storage} :components}]
  (handle-response! (ctrl-todos/create-todo! storage body)))

(defn get-todo
  [{{id :id} :path-params {storage :storage} :components}]
  (handle-response! (ctrl-todos/get-todo storage id)))

(defn get-todos
  [{{storage :storage} :components}]
  (handle-response! (ctrl-todos/get-todos storage)))

(defn update-todo
  [{{id :id} :path-params body :json-params {storage :storage} :components}]
  (handle-response! (ctrl-todos/update-todo! storage id body)))

(defn delete-todo
  [{{id :id} :path-params {storage :storage} :components}]
  (handle-response! (ctrl-todos/delete-todo! storage id)))

(def common-interceptors
  [(body-params/body-params) http/json-body])

(def routes
  #{["/" :get (conj common-interceptors `home-page)]
    ["/todo/:id" :get (conj common-interceptors `get-todo)]
    ["/todo" :get (conj common-interceptors `get-todos)]
    ["/todo" :post (conj common-interceptors `create-todo)]
    ["/todo/:id" :put (conj common-interceptors `update-todo)]
    ["/todo/:id" :delete (conj common-interceptors `delete-todo)]})
