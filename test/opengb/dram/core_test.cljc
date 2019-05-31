(ns opengb.dram.core-test
  (:require
    [clojure.spec.alpha :as s]
    [clojure.test :as t :refer [deftest is testing]]
    [opengb.dram.core :as sut :refer [Q_]]))

; (deftest can-tests-fail?
;   (is (= 1 2)))

(deftest basic-structure
  (testing "creating"
    (is (= [1 :m2] (sut/make-quantity 1 :m2)))
    (is (= [1 :m2] (Q_ 1 :m2))))
  (testing "extracting"
    (let [q (sut/make-quantity 1 :m2)]
      (is (= 1 (sut/get-magnitude q)))
      (is (= :m2 (sut/get-unit q)))))
  (testing "specs correctly report s/valid? for valid qtys"
    (is (s/valid? ::sut/quantity [1 :m2]))
    (is (s/valid? ::sut/magnitude 1))
    (is (s/valid? ::sut/magnitude 1.0))
    (is (s/valid? ::sut/unit :m2)))
  (testing "specs correctly report s/valid? for invalid qtys"
    (is (not (s/valid? ::sut/quantity [1 :bad-unit])))
    (is (not (s/valid? ::sut/quantity [:m2 1])))
    (is (not (s/valid? ::sut/magnitude "not-a-number")))
    (is (not (s/valid? ::sut/unit :any-old-keyword))))
  (testing "quantity?"
    (is (sut/quantity? (Q_ 1 :m2)))
    (is (not (sut/quantity? [:m2 1])))))
