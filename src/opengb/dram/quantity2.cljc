(ns opengb.dram.quantity2
  "deftype-based implementation"
  (:require
   [clojure.set :refer [union]]
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]))

(def area-unit? #{"m**2" "ft**2"})

(def eui-unit?
  #{"kBtu/ft**2/year" "GJ/m**2/year" "kWh/m**2/year" "kWh/ft**2/year"})

(def mass-per-year-unit? #{"t/year" "kg/year" "Mg/year" "lb/year"})

(def mass-intensity-unit? #{"kg/m**2/year" "t/m**2/year" "t/ft**2/year"
                            "lb/ft**2/year"})

(def volume-per-year-unit? #{"l/m**2/year"})

(def known-units
  "adding combos is in fact ridiculous ... we should split out a dimensionality type
  and some ways of composing like pint does. But this'll work for our purposes."
  (union area-unit?
         eui-unit?
         mass-per-year-unit?
         mass-intensity-unit?
         volume-per-year-unit?))

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

(defn- quantity-generator
  []
  (gen/fmap #(apply make-quantity %)
            (gen/tuple (gen/double* {:infinite? false :NaN false})
                       (s/gen known-units))))

(s/def ::quantity (s/with-gen quantity? quantity-generator))

(s/def ::area (s/and ::quantity #(area-unit? (get-unit %))))

(s/def ::energy-use-intensity (s/and ::quantity #(eui-unit? (get-unit %))))

(s/def ::mass-per-year (s/and ::quantity #(mass-per-year-unit? (get-unit %))))

(s/def ::mass-intensity (s/and ::quantity #(mass-intensity-unit? (get-unit %))))


