(ns opengb.dram.quantity2
  "deftype-based implementation"
  (:require [clojure.spec.alpha :as s]))

(def known-units #{:m2 :ft2})

(s/def ::magnitude number?)

(s/def ::unit known-units)

(defprotocol Quantifiable
  (get-magnitude [q])
  (get-unit [q]))

(deftype quantity
  [^:double mag unit]
  Quantifiable
  (get-magnitude [q] (.-mag q))
  (get-unit [q] (.-unit q)))

(defn quantity?
  [q?]
  (= (type q?) quantity))

(defn make-quantity
  [^:double mag unit]
  (if (and (s/valid? ::magnitude mag)
           (s/valid? ::unit unit))
    (->quantity mag unit)
    (throw (IllegalArgumentException.))))

(def Q_ make-quantity)
