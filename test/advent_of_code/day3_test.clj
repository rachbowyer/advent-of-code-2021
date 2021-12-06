(ns advent-of-code.day3-test
  (:require
    [advent-of-code.day3 :as d3]
    [clojure.test :refer :all]))

(deftest transpose-test
  (are [in out] (= out (#'d3/transpose in))
    [[1 0] [0 1]]     [[1 0] [0 1]]
    [[0 1] [0 0]]     [[0 0] [1 0]]))

(deftest ascii-array->binary-test
  (are [in out] (= out (#'d3/ascii-array->bin in))
    [1]         1
    [1 1]       3
    [1 0 1]     5
    [1 1 0 0]   12))

(deftest flip-ascii-array-test
  (are [in out] (= out (#'d3/flip-ascii-array in))
    []        []
    [0]       [1]
    [1]       [0]
    [1 0 1]   [0 1 0]))

(deftest day3-solution-part1-impl-test
(= 198
   (-> "input.day3.test.txt"
       (#'d3/load-data)
       (#'d3/day3-solution-part1-impl))))