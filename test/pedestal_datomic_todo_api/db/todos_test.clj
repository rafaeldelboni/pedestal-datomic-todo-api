(ns pedestal-datomic-todo-api.db.todos-test
  (:require [clojure.test :refer :all :as t]
            [datomic.api :refer :all :as d]
            [pedestal-datomic-todo-api.db :refer :all :as db]
            [pedestal-datomic-todo-api.db.todos :refer :all :as todos]))

(defn datomic-rollback-fixture [test-fn]
  (do
    (d/delete-database db/db-uri)
      (test-fn)))

(t/use-fixtures :each datomic-rollback-fixture)

(t/deftest ^:integration insert-db-test
  (let [conn (db/init-db-conn!)]
    (t/testing "should insert a todo"
      (let [inserted-todo (todos/create-todo! conn "Play Zelda II")
            selected-todo (first (todos/read-todos conn))]
        (t/is (= (:todo/id inserted-todo) (:todo/id selected-todo))))
      )))

(t/deftest ^:integration update-db-test
  (let [conn (db/init-db-conn!)]
    (t/testing "should update a todo"
      (let [inserted-todo (todos/create-todo! conn "Play Zelda II")
            updated-todo (todos/update-todo!
                                   conn
                                   (:todo/id inserted-todo)
                                   "Play Zelda LTTP"
                                   true)
            selected-todo (first (todos/read-todos conn))]
        (t/is (= (:todo/id inserted-todo) (:todo/id selected-todo)))
        (t/is (= (:todo/id updated-todo) (:todo/id selected-todo)))
        (t/is (= (:todo/text updated-todo) (:todo/text selected-todo)))
        (t/is (= (:todo/done? updated-todo) (:todo/done? selected-todo))))
      )))

(t/deftest ^:integration delete-db-test
  (let [conn (db/init-db-conn!)]
    (t/testing "should delete a todo"
      (let [inserted-todo (todos/create-todo! conn "Play Zelda II")
            deleted-todo (todos/delete-todo! conn (:todo/id inserted-todo))
            selected-todo (count (todos/read-todos conn))]
        (t/is (= (:todo/id inserted-todo) deleted-todo))
        (t/is (= selected-todo 0)))
      )))
