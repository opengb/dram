(ns opengb.dram.test-helpers
  (:require
   [opengb.dram.quantity2 :as qty :refer [quantity?]]))

(defn epsilon=*
  "are x and y less than epsilon apart?
   useful for inexact tests on floats, where close enough counts"
  [eps x y]
  (letfn [(abs [a] (if (<= a 0) (- a) a))]
    (let [difference (abs (- x y))]
      (<= difference eps))))

(defmulti epsilon=
  (fn [_eps a b]
    (cond (and (quantity? a) (quantity? b)) :quantity
          (and (number? a) (number? b))     :number)))

(defmethod epsilon= :default
  [_ _ _]
  (let [msg "oops, don't have epsilon= method for these types"]
  #?(:clj (throw (Exception. msg))
     :cljs (throw (js/Error. msg)))))

(defmethod epsilon= :number
  [eps a b]
  (epsilon=* eps a b))

(defmethod epsilon= :quantity
  [eps a b]
  (and (epsilon= eps (qty/get-magnitude a) (qty/get-magnitude b))
       (= (qty/get-unit a) (qty/get-unit b))))

