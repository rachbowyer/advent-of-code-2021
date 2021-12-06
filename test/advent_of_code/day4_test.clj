(ns advent-of-code.day4-test
  (:require
    [advent-of-code.day4 :as d4]
    [clojure.test :refer :all]))

(def ^:private example-board1
  [[22 13 17 11 0]
   [8  2  23 4  24]
   [21 9  14 16 7]
   [6  10 3  18 5]
   [1  12 20 15 19]])

(def ^:private example-board2
  [[3  15  0  2 22]
   [9  18 13 17  5]
   [19  8  7 25 23]
   [20 11 10 24  4]
   [14 21 16 12  6]])

(def ^:private example-board3
  [[14 21 17 24  4]
   [10 16 15  9 19]
   [18  8 23 26 20]
   [22 11 13  6  5]
   [2  0  12  3  7]])

(def ^:private boards
  [example-board1 example-board2 example-board3])

(def ^:private drawn-numbers
  [7 4 9 5 11 17 23 2 0 14 21 24 10 16 13 6 15 25 12 22 18 20 8 19 3 26 1])

(def ^:private example-marks
  #{[0 0] [1 0] [2 0] [3 0] [4 0]
    [3 1]
    [2 2]
    [1 3] [4 3]
    [0 4] [1 4] [4 4]})

(def ^:private example-marks2
  #{[0 0] [1 0] [2 0] [3 0]})

(deftest get-number?-test
  (are [x y n] (= n (#'d4/get-number? example-board2 [x y]))
    0 0  3
    0 3 20
    1 0 15
    2 3 10))

(deftest has-won?-test
  (are [marks result] (= result (boolean (#'d4/has-won? marks)))
    example-marks   true
    example-marks2  false))

(deftest sum-marked-numbers-test
  (is (= 188 (#'d4/sum-unmarked-numbers example-board3 example-marks))))

(deftest apply-number-test
  (are [drawn-number result]
    (= result
       (#'d4/apply-number example-board3 drawn-number #{[0 0]}))
     3 #{[0 0] [3 4]}
     40 #{[0 0]}))

(deftest winning-position?-test
  (are [marks result] (= result (#'d4/winning-position? boards marks))
     [#{} #{} #{[0 0] [1 0] [2 0] [3 0] [4 0]}]
     [[[14 21 17 24 4] [10 16 15 9 19] [18 8 23 26 20] [22 11 13 6 5] [2 0 12 3 7]]
     #{[0 0] [1 0] [3 0] [2 0] [4 0]}]

     [#{} #{} #{}]
     nil))

(deftest play-bingo-test
  (is (= 24
         (first (#'d4/play-bingo boards drawn-numbers)))))

(deftest play-and-score-test
  (is (= 4512
         (#'d4/play-and-score boards drawn-numbers)))
  (is (= 4512
         (apply #'d4/play-and-score (#'d4/load-data "input.day4.test.txt")))))

(deftest squid-win-and-score-test
  (is (= 1924
         (apply #'d4/squid-win-and-score (#'d4/load-data "input.day4.test.txt")))))
