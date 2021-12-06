(ns advent_of_code.day1
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- load-data []
  (-> "input.day1.txt"
      io/resource
      slurp
      str/split-lines
      ((partial map #(Integer/parseInt %)))))

(defn- count-increases [data]
  (reduce + (map (fn [x y] (if (> x y) 1 0)) (rest data) data)))

(defn day1-solution []
  (count-increases (load-data)))

;; 1692

(defn day1-solution-part-2 []
  (let [data                  (load-data)
        shifted-data          (rest data)
        shifted-shifted-data  (rest shifted-data)

        sliding-windows
        (map + shifted-shifted-data shifted-data data)]

    (count-increases sliding-windows)))

;; 1724
