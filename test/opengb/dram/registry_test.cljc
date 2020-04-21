(ns opengb.dram.registry-test
  (:require
   [clojure.test :as t :refer [deftest is]]
   [opengb.dram.core :as sut]))

(def STUB-REGISTRY {})

(deftest make-unit-registry
  (is (= STUB-REGISTRY (sut/make-unit-registry))))

(def time-definitions
  "See https://pint.readthedocs.io/en/0.10.1/defining.html#defining-units

   canonical_name = definition/base = unit symbol = alias 1 = alias 2 = ..."
  "hour = 60 * minute = h = hr
   second = [time] = s = sec
   minute = 60 * second = min
   millenium = 1e3 * year = _ = millenia
   dog_year = 52 * day = dy")

(deftest define-unit
  (let [ureg (sut/make-unit-registry)]
    (is (= STUB-REGISTRY (sut/define-unit ureg "dog_year = 52 * day = dy")))
    (is (= STUB-REGISTRY (sut/define-unit ureg "second = [time] = s = sec")))))

(deftest define-prefix
  (let [ureg (sut/make-unit-registry)]
    (is (= STUB-REGISTRY (sut/define-prefix ureg "kilo- =  1e3   = k-")))))

(deftest quantity-type-equality
  (let [ureg (sut/make-unit-registry)
        Q_ (partial sut/make-quantity ureg)]
    (prn (Q_ 1.0 "m"))
    (is (= (Q_ 1.0 "m") (Q_ 1.0 "m")))
    (is (not= (Q_ 1.0 "m") (Q_ 1.1 "m")))
    (is (not= (Q_ 1.0 "m") (Q_ 1.0 "ft")))))
