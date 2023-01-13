(ns opengb.dram.quantity-test
  (:require
   [clojure.spec.alpha :as s]
   [clojure.test :as t :refer [deftest is are testing]]
   [clojure.test.check.clojure-test :refer [defspec]]
   [clojure.test.check.properties :as prop #?@(:cljs [:include-macros true])]
   [opengb.dram.quantity :as q :refer [Q_]]))

; (deftest can-tests-fail?
;   (is (= 1 2)))

(deftest basic-structure
  (testing "constructor"
    (is (some? (q/make-quantity 5 "m**2")))
    (is (thrown? #?(:clj AssertionError :cljs js/Error)
                 (q/make-quantity "m**2" 5))))

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
    (is (s/valid? ::q/length (Q_ 5 "in")))
    (is (s/valid? ::q/area (Q_ 5.0 "m**2")))
    (is (s/valid? ::q/energy-use-intensity (Q_ 15.0 "kWh/m**2/year")))
    (is (s/valid? ::q/mass-per-year (Q_ 15.0 "t/year")))
    (is (s/valid? ::q/mass-intensity (Q_ 15.0 "t/m**2/year")))
    (is (s/valid? ::q/pressure (Q_ 2 "lb/ft**2")))
    (is (s/valid? ::q/pressure (Q_ 2 "kPa")))
    (is (s/valid? ::q/thermal-transmittance (Q_ 22 "Btu/hr*ft**2*°F")))
    (is (s/valid? ::q/thermal-transmittance (Q_ 5.7 "W/m**2*K")))))

(defspec quantity-generator-produces-quantities
  10
  (prop/for-all [x (s/gen ::q/quantity)]
                (q/quantity? x)))

(deftest conversions
  (is (= (Q_ 6905.9474784 "m**2")
         (q/us-customary->metric (Q_ 74335 "ft**2")))))

(deftest comparisons
  (testing "equality"
    (is (q/q= (Q_ 5 "m**2") (Q_ 5 "m**2")))
    (is (not (q/q= (Q_ 5 "m**2") (Q_ 4 "m**2")))))
  (testing "different units"
    (is (not (q/q= (Q_ 5 "m**2") (Q_ 5 "ft**2")))))
  (testing "less than"
    (is (q/q< (Q_ 5 "m**2") (Q_ 6 "m**2")))
    (is (not (q/q< (Q_ 5 "m**2") (Q_ 4 "m**2")))))
  (testing "less than or equal"
    (is (q/q<= (Q_ 5 "m**2") (Q_ 5 "m**2")))
    (is (q/q<= (Q_ 5 "m**2") (Q_ 6 "m**2")))
    (is (not (q/q<= (Q_ 5 "m**2") (Q_ 4 "m**2"))))))
