(ns advent-of-code.day21-part2-test
  (:require
    [advent-of-code.day21-part2 :as d21p2]
    [clojure.test :refer :all]))

(deftest quantum-game-test
  (is (= 444356092776315 (first (#'d21p2/quantum-game  4 8)))))
