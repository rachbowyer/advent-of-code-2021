(ns advent-of-code.day20-test
  (:require [clojure.test :refer :all]
            [advent-of-code.day20 :as d20]))

(def ^:private pic
  [[1 0 0 1 0] [1 0 0 0 0] [1 1 0 0 1] [0 0 1 0 0] [0 0 1 1 1]])

(def ^:private pic-with-border
  [[0 0 0 0 0 0 0 0 0]
   [0 0 0 0 0 0 0 0 0]
   [0 0 1 0 0 1 0 0 0]
   [0 0 1 0 0 0 0 0 0]
   [0 0 1 1 0 0 1 0 0]
   [0 0 0 0 1 0 0 0 0]
   [0 0 0 0 1 1 1 0 0]
   [0 0 0 0 0 0 0 0 0]
   [0 0 0 0 0 0 0 0 0]])

(deftest add-border-test
  (is (= pic-with-border (#'d20/add-border pic 0))))

(deftest parse-file-test
  (are [n c] (= c
                (->> "input.day20.test.txt"
                     (#'d20/parse-file)
                     (#'d20/enhance-n n)
                     (#'d20/count-lit-pixels)))
  2     35
  50   3351))
