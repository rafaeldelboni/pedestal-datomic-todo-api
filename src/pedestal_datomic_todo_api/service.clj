(ns pedestal-datomic-todo-api.service
  (:require [pedestal-datomic-todo-api.adapters :as adapters]
            [pedestal-datomic-todo-api.controllers.todos :as ctrl-todos]
            [pedestal-datomic-todo-api.schemes.todos :as sch-todos]
            [schema.core :as s]
            [reitit.pedestal :as pedestal]
            [reitit.ring :as ring]
            [reitit.http :as http]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.http.coercion :as coercion]
            [reitit.coercion.schema :as schema-coercion]
            [reitit.http.interceptors.parameters :as parameters]
            [reitit.http.interceptors.muuntaja :as muuntaja]
            [reitit.http.interceptors.multipart :as multipart]
            [muuntaja.core :as m]))

(s/defschema PlusXY
  {:x s/Int
   :y s/Int})

(def routes
  (pedestal/routing-interceptor
    (http/router
      [["/swagger.json"
        {:get {:no-doc true
               :swagger {:info {:title "my-api"
                                :description "with pedestal & reitit-http"}}
               :handler (swagger/create-swagger-handler)}}]

       ["/math"
        {:swagger {:tags ["math"]}}

        ["/plus"

         {:get {:summary "plus with spec query parameters"
                :parameters {:query PlusXY}
                :responses {200 {:body {:total s/Int}}}
                :handler (fn [{{{:keys [x y]} :query} :parameters}]
                           {:status 200
                            :body {:total (+ x y)}})}

          :post {:summary "plus with spec body parameters"
                 :parameters {:body PlusXY}
                 :responses {200 {:body {:total s/Int}}}
                 :handler (fn [{{{:keys [x y]} :body} :parameters}]
                            {:status 200
                             :body {:total (+ x y)}})}}]]]

      {:data {:coercion schema-coercion/coercion
              :muuntaja m/instance
              :interceptors [;; query-params & form-params
                             (parameters/parameters-interceptor)
                             ;; content-negotiation
                             (muuntaja/format-negotiate-interceptor)
                             ;; encoding response body
                             (muuntaja/format-response-interceptor)
                             ;; decoding request body
                             (muuntaja/format-request-interceptor)
                             ;; coercing response bodys
                             (coercion/coerce-response-interceptor)
                             ;; coercing request parameters
                             (coercion/coerce-request-interceptor)
                             ;; multipart
                             (multipart/multipart-interceptor)]}})

    ;; optional default ring handler (if no routes have matched)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/"
         :config {:validatorUrl nil}})
      (ring/create-resource-handler)
      (ring/create-default-handler))))

;(defn- ok-response
  ;[result status]
   ;(-> result
       ;ring-resp/response
       ;(ring-resp/status status)))

;(defn- bad-response
  ;([errors]
   ;(bad-response errors 500))
  ;([errors status]
   ;(let [st (if (nil? status) 500 status)]
     ;(-> {:errors errors}
         ;ring-resp/response
         ;(ring-resp/status st)))))

;(defn- handle-response!
  ;[response]
  ;(let [{result :result errors :error status :status} response]
    ;(if (some? errors)
      ;(bad-response errors status)
      ;(ok-response result status))))

;(defn home-page
  ;[request]
  ;(ring-resp/response {:message "Hello World!!"}))

;(defn create-todo
  ;[{body :json-params {storage :storage} :components}]
  ;(handle-response! (ctrl-todos/create-todo! storage body)))

;(defn get-todo
  ;[{{id :id} :path-params {storage :storage} :components}]
  ;(handle-response! (ctrl-todos/get-todo storage id)))

;(defn get-todos
  ;[{{storage :storage} :components}]
  ;(handle-response! (ctrl-todos/get-todos storage)))

;(defn update-todo
  ;[{{id :id} :path-params body :json-params {storage :storage} :components}]
  ;(handle-response! (ctrl-todos/update-todo! storage id body)))

;(defn delete-todo
  ;[{{id :id} :path-params {storage :storage} :components}]
  ;(handle-response! (ctrl-todos/delete-todo! storage id)))

;(def common-interceptors
  ;[(body-params/body-params) http/json-body])

;(def routes
  ;#{["/" :get (conj common-interceptors `home-page)]
    ;["/todo/:id" :get (conj common-interceptors `get-todo)]
    ;["/todo" :get (conj common-interceptors `get-todos)]
    ;["/todo" :post (conj common-interceptors `create-todo)]
    ;["/todo/:id" :put (conj common-interceptors `update-todo)]
    ;["/todo/:id" :delete (conj common-interceptors `delete-todo)]})
