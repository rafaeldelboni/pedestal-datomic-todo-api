(ns pedestal-datomic-todo-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [pedestal-datomic-todo-api.adapters :as adapters]
            [pedestal-datomic-todo-api.controllers.todos :as ctrl-todos]
            [ring.util.response :as ring-resp]))

(defn home-page
  [request]
  (ring-resp/response {:message "Hello World!"}))

(defn create-todo
  [{{:keys [text]} :path-params
    {:keys [storage]} :components}]
  (ctrl-todos/create-todo! storage text))

(defn get-todo
  [{{:keys [id]} :path-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (ctrl-todos/get-todo storage (adapters/str->uuid id))))

(defn get-todos
  [{{:keys [storage]} :components}]
  (ring-resp/response
    (ctrl-todos/get-todos storage)))

(defn update-todo
  [{{:keys [id]} :edn-params
    {:keys [text]} :edn-params
    {:keys [done]} :edn-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (ctrl-todos/update-todo! storage (adapters/str->uuid id) text done)))

(defn delete-todo
  [{{:keys [id]} :path-params
    {:keys [storage]} :components}]
  (ring-resp/response
    (ctrl-todos/delete-todo! storage (adapters/str->uuid id))))

(def common-interceptors
  [(body-params/body-params)
   http/html-body])

(def routes
  #{["/" :get (conj common-interceptors `home-page)]
    ["/todo/" :get (conj common-interceptors `get-todo)]
    ["/todo/:id/" :get (conj common-interceptors `get-todos)]
    ["/todo/" :post (conj common-interceptors `create-todo)]
    ["/todo/" :put (conj common-interceptors `update-todo)]
    ["/todo/" :delete (conj common-interceptors `delete-todo)]})
