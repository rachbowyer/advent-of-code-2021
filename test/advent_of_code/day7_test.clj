(ns advent-of-code.day7-test
  (:require
    [advent-of-code.day7 :as d7]
    [clojure.test :refer :all]))

(def ^:private example-data [16 1 2 0 4 2 7 1 2 14])

(deftest fuel-needed-test
  (are [distance->fuel-cost alignment-calculator result]
    (= result
       (#'d7/fuel-needed example-data distance->fuel-cost alignment-calculator))

      identity #'d7/median                        [2 37]
      #'d7/correct-distance->fuel-cost #'d7/mean  [5 168]))


