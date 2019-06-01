(ns opengb.dram.quantity-test
  (:require
    [clojure.test :as t :refer [deftest is testing]]
    [opengb.dram.quantity :as q :refer [Q_]]))

; (deftest can-tests-fail?
;   (is (= 1 2)))

(deftest basic-structure
  (testing "constructor"
    (is (some? (q/make-quantity 5 "m**2")))
    (is (thrown? AssertionError (q/make-quantity "m**2" 5))))

  (testing "destructors"
    (let [q (q/make-quantity 1 "m**2")]
      (is (= 1.0 (q/get-magnitude q)))
      (is (= "m**2" (q/get-unit q)))))

  (testing "quantity? predicate"
    (is (q/quantity? (Q_ 1 "m**2")))
    (is (not (q/quantity? ["m**2" 1])))
    (is (not (q/quantity? 'foo)))))
