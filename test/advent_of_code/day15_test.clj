(ns advent-of-code.day15-test
  (:require
    [advent-of-code.day15 :as d15]
    [clojure.test :refer :all]))

(def example-grid
  [[1 1 6 3 7 5 1 7 4 2]
   [1 3 8 1 3 7 3 6 7 2]
   [2 1 3 6 5 1 1 3 2 8]
   [3 6 9 4 9 3 1 5 6 9]
   [7 4 6 3 4 1 7 1 1 1]
   [1 3 1 9 1 2 8 1 3 7]
   [1 3 5 9 9 1 2 4 2 1]
   [3 1 2 5 4 2 1 6 3 9]
   [1 2 9 3 1 3 8 5 2 1]
   [2 3 1 1 9 4 4 5 8 1]])

(deftest end-point-test
  (is (= [2 2] (#'d15/end-point [[1 2 3] [4 5 6] [7 8 9]]))))

(deftest heuristic-test
  (are [a b expected] (= expected (#'d15/heuristic a b))
    [0 0] [3 4] 7
    [3 4] [0 0] 7
    [1 2] [2 1] 2))

(deftest get-successors-test
  (are [point expected]
    (= expected (#'d15/get-successors [[1 2 3] [4 5 6] [7 8 9]] point))
    [0 0] [[0 1] [1 0]]
    [1 1] [[1 2] [1 0] [2 1] [0 1]]
    [2 2] [[2 1] [1 2]]))

(deftest get-lowest-risk-start-end-test
  (is (= 40 (#'d15/get-lowest-risk-start-end example-grid)))
  (is (= 315 (#'d15/get-lowest-risk-start-end (#'d15/expand-grid example-grid)))))
