(ns opengb.dram.unit-test
  (:require
   [clojure.test :as t :refer [deftest testing is]]
   [opengb.dram.unit :as sut]))

(deftest foo
  (testing "parsing"
    (is [= '("m") (sut/parse-unit "m")])
    (is (= '(* "m" "m") (sut/parse-unit "m**2")))))

(deftest ->dimensionality
  (testing "dimensionality derivation"
    (is (= '(:length) (sut/->dimensionality "m")))
    (is (= '(* :length :length) (sut/->dimensionality "ft**2")))
    (is (= '(* :length :length) (sut/->dimensionality "[area]")))
    ; (is (= '(/ :length :time) (sut/->dimensionality "[velocity]")))
    ; (is (= '(* :length :length) (sut/->dimensionality "[acceleration]")))
    ; (is (= '(* :acceleration :mass) (sut/->dimensionality "[force]")))
    ; (is (= '(* :force :length) (sut/->dimensionality "[energy]")))
    ; (is (= '(/ (* :force :length) :time) (sut/->dimensionality "[power]")))
    ; (is (= '(/ (* :length :length)) (sut/->dimensionality "[intensity]")))
    ))

