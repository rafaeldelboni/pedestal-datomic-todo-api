(ns pedestal-datomic-todo-api.db.todos-test
  (:require [clojure.test :refer :all :as t]
            [com.stuartsierra.component :as component]
            [datomic.api :refer :all :as d]
            [pedestal-datomic-todo-api.db.todos :refer :all :as todos]
            [pedestal-datomic-todo-api.components.config :as config]
            [pedestal-datomic-todo-api.components.storage-datomic :as storage]
            [pedestal-datomic-todo-api.server :as server]))

(def system (atom nil))

(def config-map 
  {:db-uri "datomic:free://localhost:4334/todos-test"})

(defn- build-system-map []
  (component/system-map
    :config (config/new-config config-map)
    :storage (component/using (storage/new-storage-datomic) [:config])))

(defn datomic-rollback-fixture [test-fn]
  (do
    (d/delete-database (:db-uri config-map))
    (test-fn)))

(t/use-fixtures :each datomic-rollback-fixture)

(t/deftest ^:integration get-db-test
  (t/testing "should get specific todo"
    (let [{storage :storage} (server/start-system! (build-system-map) system)]
      (let [inserted-todo-1 (todos/create-todo! storage "Play Zelda II")
            inserted-todo-2 (todos/create-todo! storage "Play RE2 2019")
            selected-todo (todos/get-todo storage (:todo/id inserted-todo-1))]
        (t/is (= (:todo/id inserted-todo-1) (:todo/id selected-todo)))
        (t/is (= (:todo/text inserted-todo-1) (:todo/text selected-todo)))
        (t/is (= (:todo/done? inserted-todo-1) (:todo/done? selected-todo)))
        (t/is (not= (:todo/id inserted-todo-2) (:todo/id selected-todo))))
      )))

(t/deftest ^:integration insert-db-test
  (t/testing "should insert a todo"
    (let [{storage :storage} (server/start-system! (build-system-map) system)]
      (let [inserted-todo (todos/create-todo! storage "Play Zelda II")
            firtst-todo (get-in (todos/get-todos storage) [0 0])]
        (t/is (= (:todo/id inserted-todo) (:todo/id firtst-todo))))
      )))

(t/deftest ^:integration update-db-test
  (t/testing "should update a todo"
    (let [{storage :storage} (server/start-system! (build-system-map) system)]
      (let [inserted-todo (todos/create-todo! storage "Play Zelda II")
            updated-todo (todos/update-todo!
                           storage
                           (:todo/id inserted-todo)
                           "Play Zelda LTTP"
                           true)
            selected-todo (get-in (todos/get-todos storage) [0 0])]
        (t/is (= (:todo/id inserted-todo) (:todo/id selected-todo)))
        (t/is (= (:todo/id updated-todo) (:todo/id selected-todo)))
        (t/is (= (:todo/text updated-todo) (:todo/text selected-todo)))
        (t/is (= (:todo/done? updated-todo) (:todo/done? selected-todo))))
      )))

(t/deftest ^:integration delete-db-test
  (t/testing "should delete a todo"
    (let [{storage :storage} (server/start-system! (build-system-map) system)]
      (let [inserted-todo-1 (todos/create-todo! storage "Play Zelda II")
            inserted-todo-2 (todos/create-todo! storage "Play RE2 2019")
            deleted-todo (todos/delete-todo! storage (:todo/id inserted-todo-2))
            selected-todo (get-in (todos/get-todos storage) [0 0])]
        (t/is (= (:todo/id inserted-todo-2) (:todo/id deleted-todo)))
        (t/is (= (:todo/id selected-todo) (:todo/id inserted-todo-1))))
      )))
