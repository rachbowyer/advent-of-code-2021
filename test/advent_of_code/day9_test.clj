(ns advent-of-code.day9-test
  (:require
    [advent-of-code.day9 :as d9]
    [clojure.test :refer :all]))

(def ^:private example-data
  (str
    "2199943210\n"
    "3987894921\n"
    "9856789892\n"
    "8767896789\n"
    "9899965678"))

(def ^:private expected-data
  [[2 1 9 9 9 4 3 2 1 0]
   [3 9 8 7 8 9 4 9 2 1]
   [9 8 5 6 7 8 9 8 9 2]
   [8 7 6 7 8 9 6 7 8 9]
   [9 8 9 9 9 6 5 6 7 8]])

(deftest parse-str-test
  (is (= expected-data
         (#'d9/parse-str example-data))))

(deftest get-neighbour-test
  (are [co-ord expected] (= expected (#'d9/get-neighbours expected-data co-ord))
                         [0 0] [[1 0] [0 1]]
                         [1 1] [[0 1] [2 1] [1 0] [1 2]]
                         [1 0] [[0 0] [2 0] [1 1]]
                         [9 4] [[8 4] [9 3]]))

(deftest local-minima?-test
  (are [co-ord expected]
        (= expected
           (#'d9/local-minima? expected-data co-ord))
  [0 0] false
  [1 0] true))

(deftest get-lowest-points-test
  (is (= [[1 0] [9 0] [2 2] [6 4]]
         (#'d9/get-lowest-points expected-data))))

(deftest calc-risk-test
  (is (= 15
         (#'d9/calc-risk expected-data))))

(deftest get-basin-test
  (are [minima expected] (= expected
                            (count (#'d9/get-basin expected-data minima)))
    [1 0] 3
    [9 0] 9
    [2 2] 14
    [6 4] 9))