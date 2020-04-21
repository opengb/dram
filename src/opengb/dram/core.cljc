(ns opengb.dram.core
  ""
  (:require
   [clojure.spec.alpha :as s]))

;; * unit registries

(defn make-unit-registry
  "Make a default unit registry.

   The unit reg is what defines conversions and valid units."
  []
  {}) ;; stubbed to test interface

(defn define-unit
  [ureg _unit-definition]
  ureg ;; stubbed to test interface
  #_(str ureg "\n" _unit-definition))

(defn valid-unit?
  "Is the unit valid, or a combination of valid units?"
  [_ureg unit]
  ;; stubbed to test interface
  (s/valid? ::unit unit))

;; * quantities

(defprotocol IQuantity
  (get-magnitude [q])
  (get-unit [q]))

(deftype quantity
         [^:double mag unit]
  Object
  (equals [this other]
    (and (= (get-magnitude this) (get-magnitude other))
         (= (get-unit this) (get-unit other))))
  (toString [q]
    (str "<" (get-magnitude q) " " (get-unit q) ">"))
  IQuantity
  (get-magnitude [q] (.-mag q))
  (get-unit [q] (.-unit q)))

(defn quantity?
  [?q]
  (= (type ?q) quantity))

(defn make-quantity
  [unit-registry ^:double mag unit]
  {:pre [(s/valid? ::magnitude mag)
         (valid-unit? unit-registry unit)]}
  (->quantity (double mag) unit))

;; * specs

(s/def ::magnitude number?)

(s/def ::unit string?)
