(ns advent-of-code.day12-test
  (:require
    [advent-of-code.day12 :as d12]
    [clojure.test :refer :all]))

(def ^:private example-adjacency-list
  {:start #{:A :b}
   :A     #{:c :b :start :end}
   :b     #{:A :d :start :end}
   :c     #{:A}
   :d     #{:b}
   :end   #{:A :b}})

(deftest small-cave?-test
  (are [kw expected] (= expected (boolean (#'d12/small-cave? kw)))
                     :a true
                     :B false
                     :LN false
                     :dc true)

  (deftest count-paths-test
    (are [allow-ssc expected]
      (= expected (#'d12/count-paths allow-ssc example-adjacency-list))
      false 10
      true 36)

    (are [filename allow-ssc expected]
      (= expected (#'d12/count-paths allow-ssc
                    (#'d12/text->adjacency-list (#'d12/parse-file filename))))
      "input.day12.test1.txt" false 19
      "input.day12.test1.txt" true 103
      "input.day12.test2.txt" false 226
      "input.day12.test2.txt" true 3509)))




