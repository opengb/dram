(ns opengb.dram.registry-test
  (:require
   [opengb.dram.registry :as sut]
   [opengb.dram.test-helpers :refer [epsilon=]]
   [clojure.test :as t :refer [deftest testing are]] ))

(deftest parse-raw-definition
  (are
   [raw expected]
   (= expected (sut/parse-raw-definition raw))

   " meter = [length] = m = metre   "
   {:kind :unit :name "meter" :value "[length]" :aliases ["m" "metre"]}

   " yocto- = 1e-24 = y- "
   {:kind :prefix :name "yocto-" :value "1e-24" :aliases ["y-"]}

   " yard = 0.9144 * meter = yd = international_yard  # since Jul 1959 "
   {:kind :unit :name "yard" :value "0.9144 * meter" :aliases ["yd" "international_yard"]}

   " [area] = [length] ** 2 "
   {:kind :dimension :name "[area]" :value "[length] ** 2" :aliases nil}))

(deftest convert
  (testing "unit conversion"
    (let [registry (sut/default-registry)]
      (are [source destination]
           (let [[mag-in unit-in] source
                 [mag-out unit-out] destination
                 expected mag-out
                 actual (sut/convert registry unit-out unit-in
                                     mag-in)]
             (epsilon= 1e-2 expected actual))
           [1 "m"] [3.28 "ft"]
           ; [1 "ft"] [0.3049 "ft"]
           ; [1 "kBtu/m**2"] [1 "GJ/m**2"]
           ; [1 "m**2"] [10.758 "ft**2"]
           ; [1 "in"] [2.54 "cm"]
           ))))
