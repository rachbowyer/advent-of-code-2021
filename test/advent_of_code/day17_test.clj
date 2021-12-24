(ns advent-of-code.day17-test
  (:require
    [advent-of-code.day17 :as d17]
    [clojure.test :refer :all]))

(def ^:private test-target [20 30 -10 -5])

(deftest simulate-path-test
  (are [dx dy success y-max]
    (= {:success success :y-max y-max :init-dx dx :init-dy dy}
       (#'d17/simulate-path [dx dy] test-target))
    7 2 true 3
    6 3 true 6
    9 0 true 0
    6 9 true 45)

  (is (= {:success false}
         (#'d17/simulate-path [17 -4] test-target))))

(deftest search-max-path-test
  (is (= 45
         (-> (sort-by :y-max (#'d17/search-max-path test-target))
             last
             :y-max)))
  (is (= 112
         (count (#'d17/search-max-path test-target)))))
