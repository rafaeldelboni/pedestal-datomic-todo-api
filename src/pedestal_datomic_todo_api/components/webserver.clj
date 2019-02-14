(ns pedestal-datomic-todo-api.components.webserver
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http.route :as route]
            [io.pedestal.http :as http]))

(defn base-service [routes port]
  {:env          :dev
   ::http/type   :jetty
   ::http/routes #(route/expand-routes (deref routes))
   ::http/join?  false
   ::http/port   port})

(defrecord WebServer [routes storage]
  component/Lifecycle
  (start [this]
    (let [service (base-service (:routes routes) 8080)]
      (println (str ";; Starting webserver on "(::http/port service)))
      (assoc this :http-server
             (->> service
                  http/default-interceptors
                  http/dev-interceptors
                  http/create-server
                  http/start))))
  (stop [this]
    (dissoc this :http-server)
    this))

(defn new-webserver [] (map->WebServer {}))
