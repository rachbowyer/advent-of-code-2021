(ns advent-of-code.day6-test
  (:require
    [advent-of-code.day6 :as d6]
    [clojure.test :refer :all]))

(deftest count-after-n-days-test
  (are [num-fish expected] (= expected
                              (#'d6/count-after-n-days [3 4 3 1 2] num-fish))
    18 26
    80 5934))
