(ns opengb.dram.quantity
  "manipulating physical quantities"
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

(s/def ::quantity (s/cat :mag ::magnitude :unit ::unit))

(defn make-quantity
  [^:double mag unit]
  {:pre [(s/valid? ::magnitude mag)
         (s/valid? ::unit unit)]}
  (vector (double mag) unit))

(s/fdef make-quantity
        :args (s/cat :mag ::magnitude :unit ::unit)
        :ret ::quantity)

(def Q_ make-quantity)

(def get-magnitude first)

(def get-unit second)

(defn quantity?
  [x]
  (s/valid? ::quantity x))
