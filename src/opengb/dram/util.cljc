(ns opengb.dram.util)

(def ^:private is-not-a-number?
  "True when a number is actually the special `##NaN` value."
  #?(:cljs js/isNaN
     :clj #(java.lang.Double/isNaN %)))

(defn strictly-a-number?
  "Ensures `x` is a number and that it is not the special `##NaN` value."
  [x]
  (and (number? x)
       (not (is-not-a-number? x))))
