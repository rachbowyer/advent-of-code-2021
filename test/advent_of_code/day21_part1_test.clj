(ns advent-of-code.day21-part1-test
  (:require
    [advent-of-code.day21-part1 :as d21p1]
    [clojure.test :refer :all]))

(deftest play-game-test
  (is (= 739785 (#'d21p1/moves-times-loser (#'d21p1/play-game 4 8)))))