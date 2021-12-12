(ns advent-of-code.day11-test
  (:require
    [advent-of-code.day11 :as d11]
    [clojure.test :refer :all]))

(def ^:private small-example
  [[1 1 1 1 1]
  [1 9 9 9 1]
  [1 9 1 9 1]
  [1 9 9 9 1]
  [1 1 1 1 1]])

(def ^:private large-example
  [[5 4 8 3 1 4 3 2 2 3]
   [2 7 4 5 8 5 4 7 1 1]
   [5 2 6 4 5 5 6 1 7 3]
   [6 1 4 1 3 3 6 1 4 6]
   [6 3 5 7 3 8 5 4 7 8]
   [4 1 6 7 5 2 4 6 4 5]
   [2 1 7 6 8 4 1 7 2 1]
   [6 8 8 2 8 8 1 1 3 4]
   [4 8 4 6 8 4 8 5 5 4]
   [5 2 8 3 7 5 1 5 2 6]])

(deftest neighbours-test
  (are [max-x max-y co-ord result]
    (= result (#'d11/neighbours max-x max-y co-ord))
      3 4 [0 0] [[1 0] [0 1] [1 1]]
      3 4 [1 1] [[0 1] [2 1] [1 0] [1 2] [0 0] [2 2] [0 2] [2 0]]
      3 4 [1 3] [[0 3] [2 3] [1 2] [0 2] [2 2]]
      2 2 [0 1] [[1 1] [0 0] [1 0]]))

(deftest zero-flashed-test
  (let [octopuses [[1 2] [3 4]]]
    (are [newly-flashed result]
      (= result (#'d11/zero-flashed octopuses newly-flashed))
        #{[0 0] [1 1]} [[0 2] [3 0]]
        #{[0 0]} [[0 2] [3 4]])))

(deftest apply-flashes-test
  (are [octopuses flashes result]
    (= result (#'d11/apply-flashes octopuses flashes))
    [[1 2] [3 4]] [[0 1]] [[2 3] [3 5]]
    [[1 2] [3 4]] [[0 1] [1 0]] [[3 3] [4 6]]))

(deftest bump-one-test
  (are [octopuses result] (= result (#'d11/bump-one octopuses))
    [[1 2] [3 4]] [[2 3] [4 5]]))

(deftest one-cycle-test
  (is (= [[[[1 1 1 1 1] [1 9 9 9 1] [1 9 1 9 1] [1 9 9 9 1] [1 1 1 1 1]] 0 0]
          [[[3 4 5 4 3] [4 0 0 0 4] [5 0 0 0 5] [4 0 0 0 4] [3 4 5 4 3]] 9 1]
          [[[4 5 6 5 4] [5 1 1 1 5] [6 1 1 1 6] [5 1 1 1 5] [4 5 6 5 4]] 9 2]]
        (take 3 (iterate #'d11/one-cycle [small-example 0 0])))))

(deftest simulate-100-steps-test
  (is (= 1656
         (#'d11/simulate-100-steps large-example))))

(deftest synchronised-point-test
  (is (= 195
         (#'d11/synchronised-point large-example))))
