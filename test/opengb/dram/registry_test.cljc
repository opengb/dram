(ns opengb.dram.registry-test
  (:require
   [clojure.test :as t :refer [deftest is]]
   [opengb.dram.core :as sut]))

(deftest make-unit-registry
  (is (= {} (sut/make-unit-registry))))

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
  (is (= {} (sut/define-unit ureg "dog_year = 52 * day = dy")))
  (is (= {} (sut/define-unit ureg "second = [time] = s = sec")))))
