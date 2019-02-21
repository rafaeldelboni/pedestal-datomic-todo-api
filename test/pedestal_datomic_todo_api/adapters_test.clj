(ns pedestal-datomic-todo-api.adapters-test
  (:require [clojure.test :refer :all :as t]
            [pedestal-datomic-todo-api.adapters :refer :all :as adapter]))

(t/deftest str->uuid-test
  (t/testing "should convert input to uuid"
    (t/is (= (adapter/str->uuid "91861cd2-f2d2-4250-9c97-db7fc6b778d8") 
             #uuid "91861cd2-f2d2-4250-9c97-db7fc6b778d8"))))

(t/deftest todo-datomic->json-test
  (t/testing "should convert datomic output to json friendly map"
    (t/is (= (adapter/todo-datomic->json [])
             {}))
    (t/is (= (adapter/todo-datomic->json [{:todo/id 123}]) 
             {:id 123}))
    (t/is (= (adapter/todo-datomic->json [{:todo/id 123 :todo/text "t"}]) 
             {:id 123 :text "t"}))
    (t/is (= (adapter/todo-datomic->json [{:todo/id 123 :todo/text "t" :todo/done? false}]) 
             {:id 123 :text "t" :done false}))
    ))
