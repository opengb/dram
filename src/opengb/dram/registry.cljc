(ns opengb.dram.registry
  "Functions for maintaining/using the unit registry, which fully defines
   how conversion works."
  (:require
   [clojure.string :as string]
   [opengb.dram.unit :as unit]))

(defn make-registry
  "Make an empty unit registry."
  []
  {:dimension {} :prefix {} :unit {}})

(defn parse-raw-definition
  "Transform a definition string (from defaults file) into a definition map.
   Form of the line is `name = value = alias1 = alias2 # optional comment`"
  [s]
  (let [strip-trailing-comment #(first (string/split % #"#"))
        parts (as-> s $
                (strip-trailing-comment $)
                (string/split $ #"=")
                (map string/trim $))
        [name value & aliases] parts
        kind (cond
              (= (first name) \[) :dimension
              (= (last name)  \-) :prefix
              :else               :unit)]
    {:kind kind :name name :value value :aliases aliases}))

(defn add-definition
  "Adds a unit and returns a new registry."
  [reg definition-map]
  (let [{:keys [name value aliases]} definition-map
        definitions (into {name value} (for [a aliases] [a value]))]
    (update reg (:kind definition-map) merge definitions)))

(defn default-registry
  []
  (-> (make-registry)
      (add-definition {})))

(defn convert
  "backwards to allow easy partials"
  [_registry destination-unit source-unit magnitude]
  {:pre [(number? magnitude)
         (unit/same-dimensionality? destination-unit source-unit)]}
  3.28)

(comment
 (def sample-definitions
   "from defaults_en.txt"
   [" meter = [length] = m = metre "
    " yocto- = 1e-24 = y- "
    " yard = 0.9144 * meter = yd = international_yard  # since Jul 1959 "
    " [area] = [length] ** 2 "])

 (map parse-raw-definition sample-definitions)

 (add-definition
  (make-registry)
  (parse-raw-definition (first sample-definitions)))

 ;; building up a registry
 (def r
   (->> sample-definitions
        (map parse-raw-definition)
        (reduce #(add-definition %1 %2)
                (make-registry))))

 )
