(ns pedestal-datomic-todo-api.adapters-test
  (:require [clojure.test :refer :all :as t]
            [pedestal-datomic-todo-api.adapters :refer :all :as adapter]))

(t/deftest str->uuid-test
  (t/testing "should convert input to uuid"
    (t/is (= (adapter/str->uuid "91861cd2-f2d2-4250-9c97-db7fc6b778d8") 
             #uuid "91861cd2-f2d2-4250-9c97-db7fc6b778d8"))))

(t/deftest todo-datomic->json-test
  (t/testing "should convert datomic output to json friendly map"
    (t/is (= (adapter/todo-datomic->json nil)
             {}))
    (t/is (= (adapter/todo-datomic->json {:todo/id 123})
             {:id 123}))
    (t/is (= (adapter/todo-datomic->json {:todo/id 123 :todo/text "t"}) 
             {:id 123 :text "t"}))
    (t/is (= (adapter/todo-datomic->json {:todo/id 123 :todo/text "t" :todo/done? false}) 
             {:id 123 :text "t" :done false}))
    ))

(t/deftest format-result-test
  (t/testing "should format result for http"
    (t/is (= (adapter/format-result 404 {:id "asd" :text "zcv"})
             {:result {:id "asd" :text "zcv"} :status 404}))))

(t/deftest format-throw-test
  (t/testing "should add status on fthrown error for http"
    (let [error (adapter/format-throw 404 (Exception. "NotFound"))]
      (t/is (= (ex-data error)
               {:status 404}))
      (t/is (= (ex-message error)
               "NotFound")))))

(t/deftest format-error-test
  (t/testing "should format error for http"
    (let [error (adapter/format-error (adapter/format-throw 404 (Exception. "NotFound")))]
      (t/is (= error
               {:status 404, :error "NotFound"})))))
