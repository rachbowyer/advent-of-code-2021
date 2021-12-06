(ns advent-of-code.day5-test
  (:require
    [advent-of-code.day5 :as d5]
    [clojure.test :refer :all]))

(def ^:private example-data
  [[[0,9] [5,9]]
   [[8,0] [0,8]]
   [[9,4] [3,4]]
   [[2,2] [2,1]]
   [[7,0] [7,4]]
   [[6,4] [2,0]]
   [[0,9] [2,9]]
   [[3,4] [1,4]]
   [[0,0] [8,8]]
   [[5,5] [8,2]]])

(deftest parse-line-test
  (is (= [[6 4] [2 0]]
         (#'d5/parse-line "6,4 -> 2,0"))))

(deftest parse-file-test
  (is (= example-data
         (#'d5/parse-file "input.day5.test.txt"))))

(deftest line-segment->points-test
  (are [line-seg include-diagonals result]
    (= result (#'d5/line-segment->points
                {:include-diagonals include-diagonals}
                line-seg))
    '([7 0] [7 4])  false  '([7 0] [7 1] [7 2] [7 3] [7 4])
    '([0 9] [2 9])  false  '([0 9] [1 9] [2 9])
    '([2 9] [0 9])  false  '([2 9] [1 9] [0 9])
    '([5 2] [8 5])  false  []
    '([5 2] [8 5])  true   [[5 2] [6 3] [7 4] [8 5]]
    '([8 5] [5 2])  true   [[8 5] [7 4] [6 3] [5 2]]
    [[1 1] [1 1]]   false  [[1 1]]))

(deftest count-intersections-test
  (are [points result] (= result (#'d5/count-intersections points))
     [[0 0]]              {[0 0] 1}
     [[0 0] [0 2]]        {[0 0] 1, [0 2] 1}
     [[0 0] [0 2] [0 0]]  {[0 0] 2, [0 2] 1}))

(deftest count-intersecting-lines-test
  (let [data-from-file (#'d5/parse-file "input.day5.test.txt")]
    (are [include-diagonals data result]
      (= result
         (#'d5/count-intersecting-lines {:include-diagonals include-diagonals} data))
      false   example-data    5
      true    example-data    12
      false   data-from-file  5
      true    data-from-file  12)))

