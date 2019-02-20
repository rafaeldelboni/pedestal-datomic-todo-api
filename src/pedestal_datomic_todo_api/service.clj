(ns pedestal-datomic-todo-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [pedestal-datomic-todo-api.adapters :as adapters]
            [pedestal-datomic-todo-api.controllers.todos :as ctrl-todos]
            [schema.core :as s]
            [ring.util.response :as ring-resp]))

(def NewTodo {:text s/Str})

(defn- get-storage [request]
  (get-in request [:components :storage]))

(defn- bad-response [errors]
  (-> {:errors errors}
    ring-resp/response
    (ring-resp/status 500)))

(defn home-page
  [request]
  (ring-resp/response {:message "Hello World!!"}))

(defn home-page-post
  [request]
  (let [body (request :json-params)
        storage (get-storage request)]
    (if-let [errors (s/check NewTodo body)]
      (bad-response errors)
      (ring-resp/response {:message "Hello World!!" :body body}))))

(defn create-todo
  [{{:keys [text]} :json-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (->> (ctrl-todos/create-todo! storage text)
         (adapters/todo-datomic->json))))

(defn get-todo
  [{{:keys [id]} :path-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (->> (ctrl-todos/get-todo storage (adapters/str->uuid id))
         (map adapters/todo-datomic->json)
         (first))))

(defn get-todos
  [{{:keys [storage]} :components}]
  (ring-resp/response
    (->> (ctrl-todos/get-todos storage)
         (map adapters/todo-datomic->json))))

(defn update-todo
  [{{:keys [id]} :json-params
    {:keys [text]} :json-params
    {:keys [done]} :json-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (->> (ctrl-todos/update-todo!
           storage (adapters/str->uuid id) text (adapters/str->bool done))
         (adapters/todo-datomic->json))))

(defn delete-todo
  [{{:keys [id]} :path-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (->> (ctrl-todos/delete-todo! storage (adapters/str->uuid id))
         (adapters/todo-datomic->json))))

(def common-interceptors
  [(body-params/body-params) http/json-body])

(def routes
  #{["/" :get (conj common-interceptors `home-page)]
    ["/" :post (conj common-interceptors `home-page-post)]
    ["/todo/:id" :get (conj common-interceptors `get-todo)]
    ["/todo" :get (conj common-interceptors `get-todos)]
    ["/todo" :post (conj common-interceptors `create-todo)]
    ["/todo" :put (conj common-interceptors `update-todo)]
    ["/todo/:id" :delete (conj common-interceptors `delete-todo)]})
