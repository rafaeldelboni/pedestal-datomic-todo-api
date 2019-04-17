(ns pedestal-datomic-todo-api.components.webserver
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.interceptor.helpers :refer [before]]
            [io.pedestal.http :as http]
            [reitit.pedestal :as pedestal]))

(defn- add-system [service]
  (before (fn [context] (assoc-in context [:request :components] service))))

(defn system-interceptors 
  "Extend to service's interceptors to include one to inject the components
  into the request object"
  [service service-map]
  (update-in service-map
             [::http/interceptors]
             #(vec (->> % (cons (add-system service))))))

(defn base-service [config]
  {:env          :dev
   ::http/routes []
   ::http/type   :jetty
   ::http/port   (:http-port config)
   ::http/host   (:http-host config)})

(defn dev-init [routes service-map]
  (println "rou" routes service-map)
  (-> service-map
      (merge {:env                   :dev
              ;; do not block thread that starts web server
              ::http/join?           false
              ;; Content Security Policy (CSP) is mostly turned off in dev mode
              ::http/secure-headers  {:content-security-policy-settings {:object-src "none"}}
              ;; all origins are allowed in dev mode
              ::http/allowed-origins {:creds true :allowed-origins (constantly true)}})
      ;; Wire up interceptor chains
      (pedestal/replace-last-interceptor routes)
      http/default-interceptors
      http/dev-interceptors))

(defrecord WebServer [config routes storage]
  component/Lifecycle
  (start [this]
    (println 
      (str ";; Starting webserver on " (get-in config [:config :http-port])))
    (assoc this :service
           (->> (base-service (:config config))
                (dev-init (:routes routes))
                (system-interceptors this)
                http/create-server
                http/start)))

  (stop [this]
    (println (str ";; Stopping webserver"))
    (http/stop (:service this))
    (dissoc this :service)
    this))

(defn new-webserver []
  (map->WebServer {}))
