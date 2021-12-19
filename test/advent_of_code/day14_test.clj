(ns advent-of-code.day14-test
  (:require [advent-of-code.day14 :as d14]
            [clojure.test :refer :all]))

(deftest parify-test
  (is (= {"NN" 1 "NC" 1 "CB" 1} (#'d14/pairify "NNCB"))))

(deftest expand-test
  (let [[rules _] (#'d14/parse-file "input.day14.test.txt")]
    (are [input expected] (= (#'d14/pairify expected)
                             (#'d14/expand rules (#'d14/pairify input)))
    "NNCB"
    "NCNBCHB"

    "NCNBCHB"
    "NBCCNBBBCBHCB"

    "NBBBCNCCNBBNBNBBCHBHHBCHB"
    "NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB")))

(deftest ->character-frequencies-test
  (let [t "NBBNBNBBCCNBCNCCNBBNBBNBBBNBBNBBCBHCBHHNHCBBCBHCB"]
    (is (= (#'d14/->character-frequencies t (#'d14/pairify t))
           (frequencies t)))))

(deftest process-and-report-test
  (let [[rules template] (#'d14/parse-file "input.day14.test.txt")]
    (is (= 1588
           (#'d14/process-and-report 10 rules template)))))
