(ns opengb.dram.unit
  (:require
   [weavejester.dependency :as dep]))

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


(comment
 (-> (dep/graph)
     (dep/depend :area :length)
     (dep/depend :velocity :length)
     (dep/depend :velocity :time)
     (dep/depend :acceleration :velocity)
     (dep/depend :acceleration :time)
     (dep/transitive-dependencies :acceleration)
     ; (dep/topo-sort)
     )
 )
