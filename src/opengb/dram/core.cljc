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

(defn define-prefix
  [ureg _prefix-definition]
  ureg ;; stubbed to test interface
  )

(defn valid-unit?
  "Is the unit valid, or a combination of valid units?"
  [_ureg unit]
  ;; stubbed to test interface
  (s/valid? ::unit unit))

;; * quantities

(defprotocol IQuantity
  (get-magnitude [q])
  (get-unit [q]))

(deftype Quantity
         [^:double mag unit]
  Object ;; needs to be in deftype, else no protocol java.lang.Object
  (equals [this other]
    (and (= (get-magnitude this) (get-magnitude other))
         (= (get-unit this) (get-unit other))))
  (toString [q]
    (str "<" (get-magnitude q) " " (get-unit q) ">"))
  IQuantity
  (get-magnitude [q] (.-mag q))
  (get-unit [q] (.-unit q)))

#?(:cljs
   ;; not enough to do Object/equals in cljs??
   (extend-type Quantity
     IEquiv
     (-equiv [this other]
       (and (= (get-magnitude this) (get-magnitude other))
            (= (get-unit this) (get-unit other))))))

(defn quantity?
  [?q]
  (= (type ?q) Quantity))

(defn make-quantity
  [unit-registry ^:double mag unit]
  {:pre [(s/valid? ::magnitude mag)
         (valid-unit? unit-registry unit)]}
  (->Quantity (double mag) unit))

;; * specs

(s/def ::magnitude number?)

(s/def ::unit string?)
