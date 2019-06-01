(ns opengb.dram.quantity-test
  (:require
    [clojure.spec.alpha :as s]
    [clojure.test :as t :refer [deftest is are testing]]
    [clojure.test.check.clojure-test :refer [defspec]]
    [clojure.test.check.properties :as prop #?@(:cljs [:include-macros true])]
    [opengb.dram.quantity2 :as q :refer [Q_]]))

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

(deftest specs
  (testing "spec validity"
    (is (s/valid? ::q/quantity (Q_ 5.0 "m**2")))
    (is (s/valid? ::q/area (Q_ 5.0 "m**2")))
    (is (s/valid? ::q/energy-use-intensity (Q_ 15.0 "kWh/m**2/year")))
    (is (s/valid? ::q/mass-per-year (Q_ 15.0 "t/year")))
    (is (s/valid? ::q/mass-intensity (Q_ 15.0 "t/m**2/year")))))

(defspec quantity-generator-produces-quantities
  10
  (prop/for-all [x (s/gen ::q/quantity)]
                (q/quantity? x)))
