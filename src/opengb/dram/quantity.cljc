(ns opengb.dram.quantity
  "manipulating physical quantities"
  (:require
    [clojure.spec.alpha :as s]))

(def known-units #{:m2 :ft2})

(s/def ::magnitude number?)

(s/def ::unit known-units)

(s/def ::quantity (s/cat :mag ::magnitude :unit ::unit))

(defn make-quantity
  "wrap up a quantity"
  [mag unit]
  (vector mag unit))

(s/fdef make-quantity
        :args (s/cat :mag ::magnitude :unit ::unit)
        :ret ::quantity)

(def Q_ make-quantity)

(def get-magnitude first)

(def get-unit second)

(defn quantity?
  [x]
  (s/valid? ::quantity x))
