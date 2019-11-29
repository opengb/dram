(ns opengb.dram.quantity
  "manipulating physical quantities"
  (:require
    [clojure.set :refer [union]]
    [clojure.spec.alpha :as s]
    [opengb.dram.util]))

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

(s/def ::magnitude opengb.dram.util/strictly-a-number?)

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

;; ** Making Quantities

(defn can-convert-to-quantity?
  "True when the given `magnitude` and `unit` can be converted into a quantity."
  [magnitude unit]
  (and (s/valid? ::magnitude magnitude)
       (s/valid? ::unit unit)))

;; ** Intensities and Totals

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

;; ** US Customary and Metric

(defn energy-intensity-to-metric
  "Converts a US Customary energy intensity to the Metric equivalent."
  [quantity]
  (let [[mag unit] quantity]
    (if (= unit "kBtu/ft**2/year")
      [(* mag 3.155) "kWh/m**2/year"]
      (throw (ex-info "Can't convert given quantity to kWh" {:quantity quantity})))))

(defn area-to-metric
  "Converts a US Customary area to the Metric equivalent."
  [quantity]
  (let [[mag unit] quantity]
    (if (= unit "ft**2")
      [(/ mag 3.28 3.28) "m**2"]
      (throw (ex-info "Can't convert given quantity to metric" {:quantity quantity})))))

(defn us-customary-to-metric
  "Converts the given `quantity` from US Customary units to Metric."
  [quantity]
  (let [unit (get-unit quantity)]
    (cond
      (= unit "ft**2")           (area-to-metric quantity)
      (= unit "kBtu/ft**2/year") (energy-intensity-to-metric quantity)
      :otherwise                 (throw (ex-info "Only supported for ft**2 and kBtu." {:quantity quantity})))))

;; * i18n

(defn unit->ui-string
  "Formats the `unit` for display to a user."
  [unit]
  (let [cs {:mi2              "sq mi"
            :ft2              "sq ft"
            :ft2_a            "ft²/year"
            :USD_ft2_a        "USD/ft²/year"
            :m2               "m²"
            :GJ_m2_a          "GJ/m²/year"
            :kWh_m2_a         "kWh/m²/year"
            :tCO2e_a          "tCO₂e/year"
            "m**2"            "m²"
            "kWh/m**2/year"   "kWh/m²"
            "kg/m**2/year"    "kg/m²"
            "l/m**2/year"     "l/m²"
            "kBtu/ft**2/year" "kBtu/ft²/year"
            "ft**2"           "ft²"
            }]
    (get cs unit unit)))
