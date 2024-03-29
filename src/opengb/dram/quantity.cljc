(ns opengb.dram.quantity
  "manipulating physical quantities"
  (:require
   [clojure.set :refer [union]]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]))

;; * Utilities

(defn- assert-or-report
  "When `data` doesn't conform to `spec`, prints why it is not valid to std out
  and then throws an assertion error.
  In any case, evaluates to `nil`.
  Optionally, accepts a message to be printed when the data doesn't conform
  to the spec."
  ([spec data]
   (assert (or (s/valid? spec data)
               (s/explain spec data))))
  ([spec data message]
   (assert (or (s/valid? spec data)
               (s/explain spec data))
           message)))

;; * Definitions

(defn script-variant [unit]
  (-> unit
      (str/replace #"\*\*2" "²")
      (str/replace #"CO2" "CO₂")))

(defn- add-script-variants [units]
  (into units (map script-variant) units))

(def time-unit? #{"millisecond" "second" "minute" "hour" "day" "week" "month" "year"})

(def length-unit? #{"in" "ft" "yd" "mi" "mm" "cm" "m"})

(def area-unit? (add-script-variants (into #{} (map #(str % "**2")) length-unit?)))

(def energy-unit? #{"kBtu" "GJ" "kWh"})

(def eui-unit? (add-script-variants #{"kBtu/ft**2/year"
                                      "GJ/m**2/year"
                                      "kWh/m**2/year"
                                      "kWh/ft**2/year"}))

(def mass-per-year-unit? (add-script-variants #{"t/year" "kg/year" "Mg/year" "lb/year" "tCO2e" "tCO2e/year"}))

(def mass-intensity-unit? (add-script-variants #{"kg/m**2/year"
                                                 "kg/ft**2/year"
                                                 "t/m**2/year"
                                                 "t/ft**2/year"
                                                 "lb/ft**2/year"
                                                 "kgCO2e/m**2"
                                                 "kgCO2e/m**2/year"
                                                 "kgCO2e/ft**2"
                                                 "kgCO2e/ft**2/year"}))

(def volume-intensity-unit? (add-script-variants #{"l/m**2/year"
                                                   "gal/ft**2/year"}))

(def per-year-unit? #{"kWh/year" "l/year" "gal/year" "kBtu/year"})

(def thermal-transmittance-unit? (add-script-variants #{"Btu/hr*ft**2*°F"
                                                        "W/m**2*K"}))

(def pressure-unit? (add-script-variants #{"kPa" "lb/ft**2"}))

(def custom-unit? (add-script-variants #{"kgCO2e/m**2/year" "tCO2e/year" "energystar" "year"}))

(def known-units
  "adding combos is in fact ridiculous ... we should split out a dimensionality type
  and some ways of composing like pint does. But this'll work for our purposes."
  (union time-unit?
         length-unit?
         area-unit?
         energy-unit?
         eui-unit?
         mass-per-year-unit?
         mass-intensity-unit?
         volume-intensity-unit?
         per-year-unit?
         thermal-transmittance-unit?
         pressure-unit?
         custom-unit?))

(def us-customary-unit? (add-script-variants #{"ft**2"
                                               "lb/year"
                                               "t/year"
                                               "kBtu/ft**2/year"
                                               "kg/ft**2/year"
                                               "kWh/ft**2/year"
                                               "t/ft**2/year"
                                               "lb/ft**2/year"
                                               "kgCO2e/ft**2"
                                               "kgCO2e/ft**2/year"
                                               "gal/ft**2/year"
                                               "gal/year"
                                               "kBtu/year"
                                               "kBtu"}))

(def semi-imperial-unit?
  "A hash set of units that are combinations of other Metric and US Customary
  units. Sometimes used in Canada. This hash set can be used as a predicate to
  test whether a unit belongs to this particular system of measurement."
  (add-script-variants #{"ft**2"
                         "kWh/ft**2/year"
                         "tCO2e"
                         "kgCO2e/ft**2"
                         "l/ft**2/year"}))

(def metric-unit? (add-script-variants #{"m**2"
                                         "kg/m**2/year"
                                         "t/m**2/year"
                                         "GJ/m**2/year"
                                         "kg/year"
                                         "Mg/year"
                                         "kWh/m**2/year"
                                         "l/m**2/year"
                                         "kWh/year"
                                         "l/year"
                                         "kgCO2e/m**2"
                                         "kgCO2e/m**2/year"
                                         "tCO2e"
                                         "GJ"}))

(s/def ::magnitude (s/or :int int?
                         :double (s/double-in :infinite? false :NaN? false)))

(s/def ::unit known-units)

(s/def ::quantity (s/cat :mag ::magnitude :unit ::unit))

(defn make-quantity
  [^:double mag unit]
  (assert-or-report ::magnitude mag)
  (assert-or-report ::unit unit)
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

(defn quantity-of-unit?
  "Returns true if quantity matches provided unit.
  Single arity returns a predicate for that unit."
  ([unit]
   (partial quantity-of-unit? unit))
  ([unit quantity]
   (and (s/valid? ::unit unit)
        (s/valid? ::quantity quantity)
        (= unit (get-unit quantity)))))

(s/def ::time (s/and quantity? #(time-unit? (get-unit %))))

(s/def ::length (s/and quantity? #(length-unit? (get-unit %))))

(s/def ::area (s/and quantity? #(area-unit? (get-unit %))))

(s/def ::energy (s/and quantity? #(energy-unit? (get-unit %))))

(s/def ::energy-use-intensity (s/and quantity? #(eui-unit? (get-unit %))))

(s/def ::mass-per-year (s/and quantity? #(mass-per-year-unit? (get-unit %))))

(s/def ::mass-intensity (s/and quantity? #(mass-intensity-unit? (get-unit %))))

(s/def ::volume-intensity (s/and quantity? #(volume-intensity-unit? (get-unit %))))

(s/def ::thermal-transmittance (s/and quantity? #(thermal-transmittance-unit? (get-unit %))))

(s/def ::pressure (s/and quantity? #(pressure-unit? (get-unit %))))

(s/def ::us-customary (s/and quantity? #(us-customary-unit? (get-unit %))))

(s/def ::semi-imperial (s/and quantity? #(semi-imperial-unit? (get-unit %))))

(s/def ::metric (s/and quantity? #(metric-unit? (get-unit %))))

;; * Comparisons

(defn q=
  "Returns true if quantities have the same unit and magnitude."
  [a b]
  (and (= (get-unit a) (get-unit b))
       (= (get-magnitude a) (get-magnitude b))))

(defn q<
  "Returns true if quantities have the same unit and the magnitude of a is less
  than the magnitude of b."
  [a b]
  (and (= (get-unit a) (get-unit b))
       (< (get-magnitude a) (get-magnitude b))))

(defn q<=
  "Returns true if quantities have the same unit and the magnitude of a is less
  than or equal to the magnitude of b."
  [a b]
  (and (= (get-unit a) (get-unit b))
       (<= (get-magnitude a) (get-magnitude b))))

;; * Conversions

;; ** Making Quantities

(defn can-convert-to-quantity?
  "True when the given `magnitude` and `unit` can be converted into a quantity."
  [magnitude unit]
  (and (s/valid? ::magnitude magnitude)
       (s/valid? ::unit unit)))

;; ** Intensities and Totals

(defn intensity->total
  [intensity area]
  (assert-or-report ::metric intensity "Only Metric intensities are supported.")
  (assert-or-report ::metric area "Only Metric areas are supported.")
  (let [intensity-magnitude (get-magnitude intensity)
        area-magnitude      (get-magnitude area)
        total-energy-unit
        (cond
          (s/valid? ::energy-use-intensity intensity) "kWh/year"
          (s/valid? ::volume-intensity intensity)     "l/year"
          :else
          (throw
           (ex-info
            "Only supported for Metric volume and energy intensities."
            {:intensity intensity
             :area      area})))]
    (make-quantity (* intensity-magnitude
                      area-magnitude)
                   total-energy-unit)))

(comment
  ;; I have a Metric volume usage intensity; I want total volume usage
  (def volume-use-intensity (make-quantity 1.2 "l/m**2/year"))
  ;; => [1.2 "l/m**2/year"]
  (def area (make-quantity 400 "m**2"))
  ;; => [400.0 "m**2"]
  (def total-use (intensity->total volume-use-intensity area))
  ;; => [480.0 "l/year"]
  )

;; ** US Customary and Metric

(defn us-customary->metric
  "Converts the given `quantity` from US Customary units to Metric.

  Conversion factors are taken from using Pint."
  [quantity]
  (let [mag  (get-magnitude quantity)
        unit (get-unit quantity)]
    (cond
      (= unit "ft**2")           (make-quantity (* mag 0.3048 0.3048) "m**2")
      (= unit "kBtu/ft**2/year") (make-quantity (* mag 3.155) "kWh/m**2/year")
      (= unit "kg/ft**2/year")   (make-quantity (* mag 10.76)
                                                "kg/m**2/year")
      (= unit "kBtu")            (make-quantity (* mag 0.001055) "GJ")
      :else                      (throw (ex-info
                                         "Only supported for ft**2 and kBtu."
                                         {:quantity quantity})))))

(comment
  ;; I've got an area in US Customary units; I want the Metric equivalent
  (def us-customary-area (make-quantity 1200 "ft**2"))
  ;; => [1200.0 "ft**2"]
  (def metric-area (us-customary->metric us-customary-area))
  ;; => [111.54074955383702 "m**2"]
  )

;; * i18n

(defn unit->ui-string
  "Formats the `unit` for display to a user."
  [unit]
  (let [cs {:mi2               "sq mi"
            :ft2               "sq ft"
            :ft2_a             "ft²/year"
            :USD_ft2_a         "USD/ft²/year"
            :m2                "m²"
            :GJ_m2_a           "GJ/m²/year"
            :kWh_m2_a          "ekWh/m²/year" ;; HACK: we should have separate definitions for 'kWh/m**2/year' and 'ekWh/m**2/year'!
            :tCO2e_a           "tCO₂e/year"
            "m**2"             "m²"
            "kWh/m**2/year"    "ekWh/m²" ;; HACK: we should have separate definitions for 'kWh/m**2/year' and 'ekWh/m**2/year'!
            "GJ/m**2/year"     "GJ/m²"
            "kg/m**2/year"     "kg/m²"
            "kg/ft**2/year"    "kg/ft²"
            "l/m**2/year"      "l/m²"
            "kBtu/ft**2/year"  "kBtu/ft²/year"
            "ft**2"            "ft²"
            "kgCO2e/m**2/year" "kgCO₂e/m²"
            "kgCO2e/ft**2/year" "kgCO₂e/ft²"
            "tCO2e/year"       "tCO₂e"
            "energystar"       ""
            "year"             ""
            "gal/ft**2/year"   "gal/ft²/year"}]
    (get cs unit unit)))
