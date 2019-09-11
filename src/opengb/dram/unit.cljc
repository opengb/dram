(ns opengb.dram.unit)

(defn parse-unit
  [s]
  (case s
    "m" '("m")
    "ft" '("ft")
    "m**2" '(* "m" "m")
    "ft**2" '(* "ft" "ft")))

(defn ->dimensionality
  [s]
  (case s
    "m" '(:length)
    "ft" '(:length)
    "m**2" '(* :length :length)
    "ft**2" '(* :length :length)
    "[area]" '(* :length :length)))

(defn same-dimensionality?
  [& units]
  (let [dimensionalities (map ->dimensionality units)]
    (apply = dimensionalities)))

