(ns advent-of-code.day7
  (:require
    [clojure.string :as str]
    [clojure.java.io :as io])
  (:import
    [java.lang Math]))

;; https://adventofcode.com/2021/day/7

(defn- parse-file [filename]
  (map #(Integer/parseInt %)
       (str/split (->> filename io/resource slurp) #",")))

;; Rosetta stone version of median

(defn- median [ns]
  (let [ns (sort ns)
        cnt (count ns)
        mid (bit-shift-right cnt 1)]
    (if (odd? cnt)
      (nth ns mid)
      (/ (+ (nth ns mid) (nth ns (dec mid))) 2))))

(defn- mean [data]
  (let [cnt (count data)]
    (/ (reduce + data) cnt)))

(def ^:private round-up (comp int inc))

(def ^:private round-down int)

(defn- fuel-cost [data distance->fuel-cost position]
  (->> data
       (map (fn [h] (distance->fuel-cost (Math/abs (- position h)))))
       (reduce +)))

(defn- fuel-needed [data distance->fuel-cost alignment-calculator]
  (let [raw-position (alignment-calculator data)
        positions ((juxt round-up round-down) raw-position)
        fuel-cost-calc (partial fuel-cost data distance->fuel-cost)]
    [(apply min-key fuel-cost-calc positions)
     (apply min (map fuel-cost-calc positions))]))

(defn- correct-distance->fuel-cost [distance]
  (/ (* distance (inc distance)) 2))


;; The original assumption was that the fuel cost was the same as
;; the distance. The minimizer of the sum of absolute deviations is the median.

;; The correct assumption is that the fuel cost is the sum of an arithmetic
;; progression - so d(d+1)/2 where d is the distance - which is
;; ((d+1/2)^2 - 1/4) / 2
;; The alignment position that minimises this distance is the same alignment
;; position that minimizes d^2 (note d is non-negative), which is the mean

;; The only tricky thing is moving from the exact solution to an integer
;; solution. But the function we are optimising is a quadratic in p - the
;; alignment position, so increases in either direction from the minimum.
;; Therefore, the optimal integer solution is either the floor or the ceiling
;; of the mean.

(defn day7-solution-part1 []
  (-> "input.day7.txt"
      parse-file
      (fuel-needed identity median)
      second))

;; 328262

(defn day7-solution-part2 []
  (-> "input.day7.txt"
      parse-file
      (fuel-needed correct-distance->fuel-cost mean)
      second))

;; 90040997



