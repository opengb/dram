(ns opengb.dram.quantity2
  "deftype-based implementation"
  (:require
    [clojure.spec.alpha :as s]))

(def known-units
  "adding combos is in fact ridiculous ... we should split out a dimensionality type
  and some ways of composing like pint does. But this'll work for our purposes."
  #{:m2 :ft2
    ;; pint-style strings
    "m**2" "ft**2" ;; area
    "kBtu/ft**2/year" "GJ/m**2/year" "kWh/m**2/year" "kWh/ft**2/year" ;; eui
    "m**3/m**2" "l/m**2" ;; water use intensity
    "t/year" "kg/year", "lb/year" ;; total CO2
    "t/m**2/year" "t/ft**2/year" "lb/ft**2/year" ;; CO2
    })

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
  {:pre [(s/valid? ::magnitude mag)
         (s/valid? ::unit unit)]}
  (->quantity (double mag) unit))

(def Q_
  "pint-style shorthand for literals"
  make-quantity)
