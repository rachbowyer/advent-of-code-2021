(ns advent-of-code.day19-test
  (:require
    [advent-of-code.day19 :as d19]
    [clojure.test :refer :all]))

(deftest mat-mult-test
  (are [m v result] (= result (#'d19/mat-mult m v))
    [[1 2] [3 4]] [5 6] [17 39]))

(deftest create-seafloor-map-test
  (let [[f & r]       (#'d19/parse-file "input.day19.test.txt")
        seafloor-map  (#'d19/create-seafloor-map f r)]
    (is (= 79
           (-> seafloor-map :mapped count)))
    (is (= 3621
           (-> seafloor-map :offsets (#'d19/largest-manhattan-distance))))))
