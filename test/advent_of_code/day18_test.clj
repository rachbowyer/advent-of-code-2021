(ns advent-of-code.day18-test
  (:require [advent-of-code.day18 :as d18]
            [clojure.test :refer :all]
            [clojure.zip :as z]))

(def ^:private example1
  [[[[[9,8],1],2],3],4])

(def ^:private example2
  [[[[1,2] 3] [[[4,5] 6] 7]]  [[[[[8,9] 10]] 11] 12]])

(def ^:private example3
  [7,[6,[5,[4,[3,2]]]]])

(def ^:private example4
  [[6,[5,[4,[3,2]]]],1])

(def ^:private example5
  [[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]])

(def ^:private example6
  [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]])

(def ^:private example7
  [[[[0,7],4],[15,[0,13]]],[1,1]])

(def ^:private example8
  [[[[0,7],4],[[7,8],[0,13]]],[1,1]])

(def ^:private example9
  [[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]])

(def ^:private example10
  [[[[4,3],4],4],[7,[[8,4],9]]])

(def ^:private example11
  [1,1])

(deftest pair?-test
  (are [in out] (= out (#'d18/pair? (z/vector-zip in)))
    1           false
    [1 2]       true
    [[1 2] 3]   false))

(deftest literal?-test
  (are [in out] (= out (#'d18/literal? (z/vector-zip in)))
    1           true
    [1 2]       false
    [[1 2] 3]   false))

(deftest get-node-test
  (let [node (#'d18/get-pair-to-explode (z/vector-zip example2))]
    (is (= 3 (z/node (#'d18/get-node :left node))))
    (is (= 6 (z/node (#'d18/get-node :right node)))))
  (is (= 1
         (z/node (#'d18/get-node :left (-> [1 2] z/vector-zip z/down z/right)))))
  (is (nil? (#'d18/get-node :right (-> [1 2] z/vector-zip z/down z/right))))
  (is (nil? (#'d18/get-node :left (-> [1 2] z/vector-zip z/down z/left))))
  (is (= 2
         (z/node (#'d18/get-node :right (-> [1 2] z/vector-zip z/down))))))

(deftest get-pair-to-explode-test
  (are [in out] (= out
                   (z/node (#'d18/get-pair-to-explode (z/vector-zip in))))
    example1 [9 8]
    example2 [4 5]
    example3 [3 2]
    example4 [3 2]
    example5 [7 3]
    example6 [3 2]))

(deftest explode-test
  (are [in out] (= [true out] (#'d18/explode in))
      example1 [[[[0 9] 2] 3] 4]
      example3 [7,[6,[5,[7,0]]]]
      example4 [[6,[5,[7,0]]],3]
      example5 [[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]
      example6 [[3,[2,[8,0]]],[9,[5,[7,0]]]]))

(deftest split-test
  (are [in out] (= [true out] (#'d18/split in))
    example7 example8
    example8 [[[[0,7],4],[[7,8],[0,[6,7]]]],[1,1]]))

(deftest reduce-shellfishnum
  (is (= [[[[0 7] 4] [[7 8] [6 0]]] [8 1]]
         (#'d18/reduce-shellfishnum example9))))

(deftest add-list-test
  (are [in out] (= out (#'d18/add-list in))
    [[[[1,1],[2,2]],[3,3]],[4,4]] [[[[1 1] [2 2]] [3 3]] [4 4]]
    [[[[1,1],[2,2]],[3,3]],[4,4],[5,5]] [[[[3 0] [5 3]] [4 4]] [5 5]]
    [[[[1,1],[2,2]],[3,3]],[4,4],[5,5],[6,6]] [[[[5 0] [7 4]] [5 5]] [6 6]])

  (is (= [[[[8 7] [7 7]] [[8 6] [7 7]]] [[[0 7] [6 6]] [8 7]]]
         (#'d18/add-list (#'d18/parse-file "input.day18.test.txt")))))

(deftest add-magnitude-test
  (are [in out] (= out
                   (#'d18/magnitude in))
    [9 1]             29
    [[1,2],[[3,4],5]] 143
    [[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]] 3488))