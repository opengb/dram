(ns opengb.dram.quantity
  "manipulating physical quantities"
  (:require
    [clojure.set :refer [union]]
    [clojure.spec.alpha :as s]))

(def length-unit? #{"m" "ft" "in"})

(def area-unit? #{"m**2" "ft**2"})

(def pressure-unit? #{"lb/ft**2" "kPa"})

(def eui-unit?
  #{"kBtu/ft**2/year" "GJ/m**2/year" "kWh/m**2/year" "kWh/ft**2/year"})

(def mass-per-year-unit? #{"t/year" "kg/year" "Mg/year" "lb/year"})

(def mass-intensity-unit? #{"kg/m**2/year" "t/m**2/year" "t/ft**2/year"
                            "lb/ft**2/year"})

(def volume-per-year-unit? #{"l/m**2/year"})

(def thermal-transmittance-unit? #{"Btu/hr*ft**2*°F" "W/m**2*K"}) ;; U-value

(def known-units
  "adding combos is in fact ridiculous ... we should split out a dimensionality type
  and some ways of composing like pint does. But this'll work for our purposes."
  (union length-unit?
         area-unit?
         pressure-unit?
         eui-unit?
         mass-per-year-unit?
         mass-intensity-unit?
         volume-per-year-unit?
         thermal-transmittance-unit?))

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

(s/def ::area (s/and quantity? #(area-unit? (get-unit %))))

(s/def ::energy-use-intensity (s/and quantity? #(eui-unit? (get-unit %))))

(s/def ::mass-per-year (s/and quantity? #(mass-per-year-unit? (get-unit %))))

(s/def ::mass-intensity (s/and quantity? #(mass-intensity-unit? (get-unit %))))
