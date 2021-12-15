(ns advent-of-code.day13-test
  (:require
    [advent-of-code.day13 :as d13]
    [clojure.test :refer :all]))

(def ^:private points
  #{[6, 10]
    [0, 14]
    [9, 10]
    [0, 3]
    [10, 4]
    [4, 11]
    [6, 0]
    [6, 12]
    [4, 1]
    [0, 13]
    [10, 12]
    [3, 4]
    [3, 0]
    [8, 4]
    [1, 10]
    [2, 14]
    [8, 10]
    [9, 0]})

(def ^:private  operations
  [(partial #'d13/fold-along-y 7)
   (partial #'d13/fold-along-x 5)])

(deftest apply-folds-test
  (are [pts ops result] (= result (count (#'d13/apply-folds pts ops)))
    points (take 1 operations) 17
    points operations 16))


