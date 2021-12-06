(ns advent-of-code.day6
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

;; https://adventofcode.com/2021/day/6

(defn- parse-file [filename]
  (map #(Integer/parseInt %)
       (str/split (->> filename io/resource slurp) #",")))

(defn- make-frequency-array [lantern-fish]
  (let [frequency-map (frequencies lantern-fish)]
    (->> (range 9) (map (fn [e] (or (frequency-map e) 0))) vec)))

(defn- simulate-one-day [[day-zero & remainder]]
  (-> remainder vec (conj day-zero) (update 6 + day-zero)))

(defn- simulate-n-days [days lantern-fish-array]
  (nth (iterate simulate-one-day lantern-fish-array) days))

(defn- count-after-n-days [init-lantern-fish days]
  (->> init-lantern-fish
       make-frequency-array
       (simulate-n-days days)
       (reduce +)))

(defn day6-solution-part1 []
  (count-after-n-days (parse-file "input.day6.txt") 80))

;; 350149

(defn day6-solution-part2 []
  (count-after-n-days (parse-file "input.day6.txt") 256))

;; 1590327954513

;; This was a fun puzzle :-)
