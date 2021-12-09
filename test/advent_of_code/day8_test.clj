(ns advent-of-code.day8-test
  (:require
    [advent-of-code.day8 :as d8]
    [clojure.test :refer :all]))

(def example-input-segment-patterns
  "acedgfb cdfbe gcdfa fbcad dab cefabd cdfgeb eafb cagedb ab")

(def example-output-segment-patterns
  "cdfeb fcadb cdfeb cdbaf")

(deftest ->numbers-test
  (let [input   (#'d8/parse-segment-patterns example-input-segment-patterns)
        output  (#'d8/parse-segment-patterns example-output-segment-patterns)
        decoded (#'d8/->numbers input output)]
    (is (= [5 3 5 3] decoded))))
