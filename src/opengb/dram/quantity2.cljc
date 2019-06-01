(ns opengb.dram.quantity2
  "deftype-based implementation"
  (:require [clojure.spec.alpha :as s]))

(def known-units #{:m2 :ft2})

(s/def ::magnitude number?)

(s/def ::unit known-units)

(defprotocol IQuantity
  (get-magnitude [q])
  (set-magnitude! [q mag'])
  (get-unit [q])
  (convert [q unit]))

;; perf gain for reductions?
;; https://stackoverflow.com/questions/3132931/mutable-fields-in-clojure-deftype
(deftype quantity
  [^{#_#_:volatile-mutable true :double true} mag unit]
  IQuantity
  (get-magnitude [q] (.-mag q))
  ; (set-magnitude! [q mag'] (set! (.-mag q) mag'))
  (get-unit [q] (.-unit q)))

(defn quantity?
  [q?]
  (= (type q?) quantity))

(defn make-quantity
  [^:double mag unit]
  (if (and (s/valid? ::magnitude mag)
           (s/valid? ::unit unit))
    (->quantity (double mag) unit)
    (throw (IllegalArgumentException.))))

(def Q_
  "pint-style shorthand for literals"
  make-quantity)
