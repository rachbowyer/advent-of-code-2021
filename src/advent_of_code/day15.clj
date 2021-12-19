(ns advent-of-code.day15
  (:require [clojure.data.priority-map :refer [priority-map]]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- parse-file [file]
  (->> file io/resource slurp str/split-lines
       (mapv (fn [e] (mapv #(Integer/parseInt (str %)) e)))))

(def ^:private expansion-constant 5)

(def ^:private moves [[0 1] [0 -1] [1 0] [-1 0]])

(defn size-x [grid]
  (-> grid first count))

(defn size-y [grid]
  (count grid))

(defn get-value [grid [x y]]
  (get (get grid y) x))

(def start-point [0 0])

(defn end-point [grid]
  [(-> grid size-x dec) (-> grid size-y dec)])

(defn- expand [direction block]
  (loop [i          1
         last-block block
         result     block]
    (if (= i expansion-constant)
      (vec result)
      (let [f (fn [r] (mapv #(if (= % 9) 1 (inc %)) r))
            new-block (if (= direction :across)
                        (f last-block)
                        (map f last-block))]
        (recur (inc i) new-block (concat result new-block))))))

(defn- expand-grid [grid]
  (->> grid (mapv (partial expand :across)) (expand :down )))

(defn- heuristic
  [[x1 y1] [x2 y2]]
  (+ (Math/abs (- x2 x1)) (Math/abs (- y2 y1))))

(defn- get-successors [grid [x y]]
  (->> moves
       (map (fn [[x' y']] [(+ x x') (+ y y')]))
       (remove (fn [[x y]] (or (neg? x) (neg? y)
               (>= x (size-x grid)) (>= y (size-y grid)))))))

(defn- get-lowest-risk
  [grid start-point end-point]
  (loop [explored #{start-point}
         priority-queue (priority-map [start-point 0] ; distance from start point
                                      (heuristic start-point end-point))]
    (let [[[node distance] _] (peek priority-queue)]
      (if (= node end-point)
        distance
        (let [new-queue
              (->> (get-successors grid node)
                   (remove explored)
                   (map (fn [s]
                          (let [distance-to-node (+ distance (get-value grid s))]
                            [[s distance-to-node]
                             (+ distance-to-node (heuristic s end-point))])))
                   (reduce conj (pop priority-queue)))]
          (recur (conj explored node) new-queue))))))

(defn- get-lowest-risk-start-end [grid]
  (get-lowest-risk grid start-point (end-point grid)))

(defn day15-solution-part1 []
  (-> "input.day15.txt" parse-file get-lowest-risk-start-end))

(defn day15-solution-part2 []
  (-> "input.day15.txt" parse-file expand-grid get-lowest-risk-start-end))

;; My original solution used dynamic programming. However, this only works
;; if the optimal path is always either down or right; this is not the case.
;;
;; New and correct solution uses A* search.
;;
;; The Heuristic is based on Manhattan distance
;; 1. It is admissible. This means that A* finds the minimum distance
;; 2. It is complete. This means that first time it sees a node it has found the
;;    shortest distance to the node. This avoids needing to update distances
;;    to successors later."
;;
;; If the heuristic is set to 0 (then becomes a uniform cost search). Funnily
;; enough this speeds things up as the heuristic is not helping much and takes
;; time to calculate

