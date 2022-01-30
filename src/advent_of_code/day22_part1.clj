(ns advent-of-code.day22-part1
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def ^:private regex
  #"(\w+) x=([-\d]+)..([-\d]+),y=([-\d]+)..([-\d]+),z=([-\d]+)..([-\d]+)")

(defn- parse-line [line]
  (->> line
       (re-find regex)
       rest
       (mapv (fn [e] (case e "on" :on "off" :off (Integer/parseInt e))))))

(defn- parse-lines [lines]
  (map parse-line lines))

(defn- parse-file [filename]
  (->> filename io/resource slurp str/split-lines parse-lines))

(defn- create-range [x1 x2]
  (let [lower (min x1 x2)
        upper (max x1 x2)]
    (range (max -50 lower) (inc (min 50 upper)))))

(defn- expand-cuboid [x1 x2 y1 y2 z1 z2]
  (for [x (create-range x1 x2) y (create-range y1 y2) z (create-range z1 z2)]
    [x y z]))

(defn- reboot-reactor [commands]
  (reduce (fn [reactor [direction x1 x2 y1 y2 z1 z2]]
            (reduce
              (fn [reactor cube]
                (assoc reactor cube (if (= direction :on) 1 0)))
              reactor
              (expand-cuboid x1 x2 y1 y2 z1 z2)))
          {}
          commands))

(defn- cubes-on [reactor]
  (->> reactor (map val) (reduce +)))

(defn day22-solution-part1 []
  (-> "input.day22.txt" parse-file reboot-reactor cubes-on))

;; Day 2 would involve keeping a list of disjoint cuboids containing cubes that
;; are on. Each step would add adjust the list of cuboids to either turn on
;; more cubes or switch off cubes.
;; Then at the end - the cubes in each disjoint cuboid are summed.
;; My head hurts thinking about writing the logic

;; Reading Reddit some people solved it exactly this way. Other people kept
;; a list of positive and negative cuboids. As each cuboid was added, a list
;; of intersections with the existing (+ve, -ve) cuboids are kept and +ve or
;; -ve cuboids are generated as appropriate.
