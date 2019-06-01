(ns opengb.dram.quantity-test
  (:require
    [clojure.test :as t :refer [deftest is testing]]
    [opengb.dram.quantity :as q :refer [Q_]]))

; (deftest can-tests-fail?
;   (is (= 1 2)))

(deftest basic-structure
  (testing "making"
    (is (some? (q/make-quantity 5 :m2)))
    (is (thrown? IllegalArgumentException
                 (q/make-quantity :m2 5))))

  (testing "extracting parts"
    (let [q (q/make-quantity 1 :m2)]
      (is (= 1 (q/get-magnitude q)))
      (is (= :m2 (q/get-unit q)))))

  (testing "quantity? predicate"
    (is (q/quantity? (Q_ 1 :m2)))
    (is (not (q/quantity? [:m2 1])))
    (is (not (q/quantity? 'foo)))))
