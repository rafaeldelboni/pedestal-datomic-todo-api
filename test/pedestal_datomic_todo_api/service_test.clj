(ns pedestal-datomic-todo-api.service-test
  (:require [clojure.test :refer :all :as t]
            [cheshire.core :as json]
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

(defn parsed-response-body [response-body]
  (json/decode (:body response-body) true))

(def request-parsed
  (comp parsed-response-body do-request))

(defn datomic-rollback-fixture [test-fn]
  (do
    (d/delete-database (:db-uri config-map))
    (server/start-system! (build-system-map) system)
    (test-fn)
    (server/stop-system! system)))

(t/use-fixtures :each datomic-rollback-fixture)

(t/deftest ^:integration home-page-test
  (t/testing "should get hello world"
    (let [service (get-service-fn system)]
      (t/is (= (:body (pt/response-for service :get "/"))
               "{\"message\":\"Hello World!!\"}")))))

(t/deftest ^:integration get-todo-test
  (t/testing "should get specific todo"
    (let [service (get-service-fn system)]
      (let [inserted-todo-1 (request-parsed
                              service :post "/todo" "{\"text\": \"Play Zelda II\"}")
            inserted-todo-2 (request-parsed
                              service :post "/todo" "{\"text\": \"Play RE2 2019\"}")
            selected-todo (request-parsed
                            service :get (str "/todo/" (:id inserted-todo-1)) "")
            all-todos (request-parsed service :get "/todo" "")]
        (t/is (= (:id inserted-todo-1) (:id selected-todo)))
        (t/is (= (:text inserted-todo-1) (:text selected-todo)))
        (t/is (= (:done inserted-todo-1) (:done selected-todo)))
        (t/is (not= (:id inserted-todo-2) (:id selected-todo)))
        (t/is (= (count all-todos) 2))
        ))))

(t/deftest ^:integration create-todo-test
  (t/testing "should create and list a todo"
    (let [service (get-service-fn system)]
      (let [created-todo
            (request-parsed service :post "/todo" "{\"text\": \"test\"}")]
        (t/is (= (:text created-todo) "test"))
        (t/is (= (:done created-todo) false))
        ))))

(t/deftest ^:integration update-todo-test
  (t/testing "should update a todo"
    (let [service (get-service-fn system)]
      (let [inserted-todo (request-parsed
                              service :post "/todo" (json/encode {:text "Play Zelda II"}))
            updated-todo (request-parsed
                           service :put "/todo"
                           (json/encode {:id (:id inserted-todo)
                                         :text "Play Zelda LTTP"
                                         :done true}))
            selected-todo (first (request-parsed service :get "/todo" ""))]
        (t/is (= (:id inserted-todo) (:id selected-todo)))
        (t/is (= (:id updated-todo) (:id selected-todo)))
        (t/is (= (:text updated-todo) (:text selected-todo)))
        (t/is (= (:done updated-todo) (:done selected-todo))))
      )))

(t/deftest ^:integration delete-todo-test
  (t/testing "should delete a todo"
    (let [service (get-service-fn system)]
      (let [inserted-todo-1 (request-parsed
                              service :post "/todo" "{\"text\": \"Play Zelda II\"}")
            inserted-todo-2 (request-parsed
                              service :post "/todo" "{\"text\": \"Play RE2 2019\"}")
            deleted-todo (request-parsed
                           service :delete (str "/todo/" (:id inserted-todo-2)) "")
            selected-todo (first (request-parsed service :get "/todo" ""))]
        (t/is (= (:id inserted-todo-2) (:id deleted-todo)))
        (t/is (= (:id selected-todo) (:id inserted-todo-1)))
        ))))
