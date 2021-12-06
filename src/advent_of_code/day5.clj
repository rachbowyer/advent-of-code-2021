(ns advent-of-code.day5
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- split-and-convert-int [line]
  (map #(Integer/parseInt %) (str/split line #",")))

(defn- parse-line [line]
  (-> line (str/split #" -> ") ((partial map split-and-convert-int))))

(defn- parse-file [filename]
  (->> filename io/resource slurp str/split-lines (map parse-line)))

(defn- line-segment->points
  [{include-diagonals :include-diagonals} [[x1 y1] [x2 y2]]]
  (let [x-inc (if (>= x1 x2) -1 1)
        y-inc (if (>= y1 y2) -1 1)]
    (cond
      (= x1 x2) (map (fn [y] [x1 y]) (range y1 (+ y2 y-inc) y-inc))
      (= y1 y2) (map (fn [x] [x y1]) (range x1 (+ x2 x-inc) x-inc))
      include-diagonals (map (fn [x y] [x y])
                             (range x1 (+ x2 x-inc) x-inc)
                             (range y1 (+ y2 y-inc) y-inc))
      :other [])))

(defn- count-intersections [points]
  (reduce (fn [acc e] (update acc e (fnil inc 0))) {} points))

(defn count-intersecting-lines [include-diagonals line-segments]
  (->> line-segments
       (mapcat (partial line-segment->points include-diagonals))
       count-intersections
       (filter #(>= (second %) 2))
       count))

(defn day5-solution-part1 []
  (->>  "input.day5.txt"
        parse-file
        (count-intersecting-lines {:include-diagonals false})))

;; 8622

(defn day5-solution-part2 []
  (->>  "input.day5.txt"
        parse-file
        (count-intersecting-lines {:include-diagonals true})))

;; 22037

;; Experimented with using {include-diagonals :include-diagonals} for a boolean
;; flag to make code more explanatory.