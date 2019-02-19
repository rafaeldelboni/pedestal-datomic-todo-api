(ns pedestal-datomic-todo-api.service-test
  (:require [clojure.test :refer :all :as t]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [datomic.api :refer :all :as d]
            [io.pedestal.test :refer :all :as pt]
            [pedestal-datomic-todo-api.components.config :as config]
            [pedestal-datomic-todo-api.components.routes :as routes]
            [pedestal-datomic-todo-api.components.storage-datomic :as storage]
            [pedestal-datomic-todo-api.components.webserver :as webserver]
            [pedestal-datomic-todo-api.service :as service]
            [pedestal-datomic-todo-api.server :as server]))

(def system (atom nil))

(def config-map
  {:db-uri (str "datomic:mem://" "mem-conn-" (d/squuid))
   :http-port 8989
   :http-host "localhost"})

(defn- build-system-map []
  (component/system-map
    :config (config/new-config config-map)
    :storage (component/using (storage/new-storage-datomic) [:config])
    :routes  (routes/new-routes #'pedestal-datomic-todo-api.service/routes)
    :http-server (component/using (webserver/new-webserver) [:config :routes :storage])))

(defn get-service-fn [system]
  (get-in @system [:http-server :service :io.pedestal.http/service-fn]))

(defn do-request [service verb route body]
  (pt/response-for
    service verb route :headers {"Content-Type" "application/json"} :body body))

(defn datomic-rollback-fixture [test-fn]
  (do
    (server/start-system! (build-system-map) system)
    (test-fn)
    (server/stop-system! system)))

(t/use-fixtures :each datomic-rollback-fixture)

(t/deftest ^:integration home-page-test
  (t/testing "should get hello world"
    (let [service (get-service-fn system)]
      (t/is (= (:body (pt/response-for service :get "/"))
               "{\"message\":\"Hello World!!\"}")))))

(t/deftest ^:integration create-todo-test
  (t/testing "should create and list a todo"
    (let [service (get-service-fn system)]
      (let [created-todo
            (:body (do-request service :post "/todo" "{\"text\": \"test\"}"))]
        (t/is (= (str/includes? created-todo "\"text\":\"test\",\"done\":false")
                 true))))))
