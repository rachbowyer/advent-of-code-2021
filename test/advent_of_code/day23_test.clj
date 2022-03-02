(ns advent-of-code.day23-test
  (:require
    [advent-of-code.day23 :as day23]
    [clojure.test :refer :all]))

(def ^:private side-rooms-x @#'day23/side-rooms-x)
(def ^:private lower-y @#'day23/lower-y)
(def ^:private upper-y @#'day23/upper-y)

(def ^:private starting-position
  "Co-ord of each amphipod"
  {:a1 [(:a side-rooms-x) lower-y]
   :a2 [(:d side-rooms-x) lower-y]
   :b1 [(:a side-rooms-x) upper-y]
   :b2 [(:c side-rooms-x) upper-y]
   :c1 [(:b side-rooms-x) upper-y]
   :c2 [(:c side-rooms-x) lower-y]
   :d1 [(:b side-rooms-x) lower-y]
   :d2 [(:d side-rooms-x) upper-y]})

(def ^:private goal-position
  "Co-ord of each amphipod"
  {:a1 [(:a side-rooms-x) lower-y]
   :a2 [(:a side-rooms-x) lower-y]
   :b1 [(:b side-rooms-x) upper-y]
   :b2 [(:b side-rooms-x) upper-y]
   :c1 [(:c side-rooms-x) upper-y]
   :c2 [(:c side-rooms-x) lower-y]
   :d1 [(:d side-rooms-x) upper-y]})

(deftest amphipod->genus-test
  (are [in out] (= out (#'day23/amphipod->genus in))
    :a1 :a
    :c2 :c))

(deftest amphipod-goals-test
  (are [in out] (= out (#'day23/amphipod-goals (#'day23/amphipod->genus in)))
    :a1 [[3 2] [3 3]]
    :c2 [[7 2] [7 3]]))

(deftest manhattan-distance-test
  (are [a b out] (= out (#'day23/manhattan-distance a b))
    [1 2]   [-3 4]  6
    [-1 2]  [-3 4]  4))

(deftest heuristic-for-amphipod-test
  (are [amphipod current-position heuristic]
    (= heuristic (#'day23/heuristic-for-amphipod amphipod current-position))
      :a1 [3 2] 0
      :a1 [3 3] 0
      :a1 [3 1] 1
      :a1 [5 3] 3
      :b2 [7 2] 20))

(deftest heuristic-test
  (is (= 5247 (#'day23/heuristic starting-position))))

(deftest get-blocked-locations-test
  (is (= #{[7 2] [3 3] [7 3] [5 3] [9 3] [5 2] [9 2] [3 2]}
         (#'day23/get-blocked-locations starting-position))))

(deftest cost-move-test
  (are [amphipod-genus blocked start end cost path]
    (= (when cost {:cost cost, :path path})
       (#'day23/cost-move blocked amphipod-genus start end))

    :a (#'day23/get-blocked-locations starting-position) [11 1] [3 1]
    8 [[4 1] [5 1] [6 1] [7 1] [8 1] [9 1] [10 1] [11 1]]

    :a (#'day23/get-blocked-locations starting-position) [3 1] [3 2]
    nil nil

    :a #{} [3 2] [5 2]
    4 [[5 1] [4 1] [3 1] [3 2]]

    :a #{[4 1]} [3 2] [5 2]
    nil nil

    :b #{} [3 2] [5 2]
    40 [[5 1] [4 1] [3 1] [3 2]]))

(deftest get-valid-destinations-test
  (let [reverse-positions (#'day23/reverse-position starting-position)]
    (are [reverse-position amphipod-genus current-pos positions]
      (= positions
         (#'day23/get-valid-destinations reverse-position amphipod-genus current-pos))

      ;; In goal location
      reverse-positions :a [3 3]
      []

      ;; In side room but not goal location, can move to corridor
      reverse-positions :b [3 2]
      [[1 1] [4 1] [8 1] [6 1] [10 1] [11 1] [2 1]]

      ;; In corridor, empty side room
      (#'day23/reverse-position (dissoc starting-position :a1 :b1)) :a [1 1]
      [[3 3]]

      ;; Corridor, empty upper side room - lower filled with same genus
      (#'day23/reverse-position (dissoc starting-position :b1)) :a  [1 1]
      [[3 2]]

      ;; Corridor, but side room not ready
      reverse-positions :a [1 1]
      []

      ;; In goal location but blocking another different genus so has to move
      reverse-positions :d [9 2]
      [[1 1] [4 1] [8 1] [6 1] [10 1] [11 1] [2 1]])))

(deftest succeeded?-test
  (are [position result] (= result (#'day23/succeeded? position))
    @#'day23/starting-position  false
    goal-position               true))
