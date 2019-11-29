(ns opengb.dram.quantity
  "manipulating physical quantities"
  (:require
    [clojure.set :refer [union]]
    [clojure.spec.alpha :as s]))

(def area-unit? #{"m**2" "ft**2"})

(def eui-unit?
  #{"kBtu/ft**2/year" "GJ/m**2/year" "kWh/m**2/year" "kWh/ft**2/year"})

(def mass-per-year-unit? #{"t/year" "kg/year" "Mg/year" "lb/year"})

(def mass-intensity-unit? #{"kg/m**2/year" "t/m**2/year" "t/ft**2/year"
                            "lb/ft**2/year"})

(def volume-intensity-unit? #{"l/m**2/year"})

(def per-year-unit? #{"kWh/year" "l/year"})

(def known-units
  "adding combos is in fact ridiculous ... we should split out a dimensionality type
  and some ways of composing like pint does. But this'll work for our purposes."
  (union area-unit?
         eui-unit?
         mass-per-year-unit?
         mass-intensity-unit?
         volume-intensity-unit?
         per-year-unit?))

(def us-customary-unit? #{"ft**2"
                          "lb/year"
                          "t/year"
                          "kBtu/ft**2/year"
                          "kWh/ft**2/year"
                          "t/ft**2/year"
                          "lb/ft**2/year"})

(def metric-unit? #{"m**2"
                    "kg/m**2/year"
                    "t/m**2/year"
                    "GJ/m**2/year"
                    "kg/year"
                    "Mg/year"
                    "kWh/m**2/year"
                    "l/m**2/year"
                    "kWh/year"
                    "l/year"})

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

(s/def ::volume-intensity (s/and quantity? #(volume-intensity-unit? (get-unit %))))

(s/def ::us-customary (s/and quantity? #(us-customary-unit? (get-unit %))))

(s/def ::metric (s/and quantity? #(metric-unit? (get-unit %))))

;; * Conversions

(defn calculate-total-water-use
  "Provides total water use in l/year."
  [intensity area]
  {:pre [(s/valid? ::volume-intensity intensity)
         (s/valid? ::metric intensity)
         (s/valid? ::area area)
         (s/valid? ::metric area)]}
  (let [water-magnitude   (get-magnitude intensity)
        area-magnitude    (get-magnitude area)
        total-volume-unit "l/year"]
    (make-quantity (* water-magnitude
                      area-magnitude)
                   total-volume-unit)))

(defn calculate-total-energy-use
  "Calculates total energy use in kWh/year."
  [intensity area]
  {:pre [(s/valid? ::energy-use-intensity intensity)
         (s/valid? ::metric intensity)
         (s/valid? ::area area)
         (s/valid? ::metric area)]}
  (let [energy-magnitude  (get-magnitude intensity)
        area-magnitude    (get-magnitude area)
        total-energy-unit "kWh/year"]
    (make-quantity (* energy-magnitude
                      area-magnitude)
                   total-energy-unit)))

(defn intensity->total
  [intensity area]
  (cond
    (s/valid? ::energy-use-intensity intensity) (calculate-total-energy-use intensity area)
    (s/valid? ::volume-intensity intensity)     (calculate-total-water-use intensity area)
    :default                                    (throw (ex-info "Only supported for volume and energy intensities." {:intensity intensity
                                                                                                                     :area      area}))))
