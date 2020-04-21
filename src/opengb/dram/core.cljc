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
  [_ureg _unit]
  true ;; stubbed to test interface
  #_(true? (get unit ureg)))

;; * quantities

(defprotocol IQuantity
  (get-magnitude [q])
  (get-unit [q]))

(deftype quantity
         [^:double mag unit]
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
