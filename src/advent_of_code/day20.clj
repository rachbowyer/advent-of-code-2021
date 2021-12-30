(ns advent-of-code.day20
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- ascii->bin [line]
  (mapv #(if (= \# %) 1 0) line))

(defn- parse-file [filename]
  (let [[algo _ & r]
        (->> filename io/resource slurp str/split-lines)]
    [(mapv ascii->bin r) 0 (ascii->bin algo)]))

(defn- add-border [pic border-colour]
  (let [row-border (repeat 2 border-colour)
        rows (mapv (fn [e] (vec (concat row-border e row-border))) pic)
        top-border (vec (repeat (-> rows first count) border-colour))]
    (vec (concat [top-border top-border] rows [top-border top-border]))))

(defn- get-window [pic [x y] b-c]
  (let [offsets (for [y [-1 0 1] x [-1 0 1]] [x y])]
    (map (fn [[dx dy]] (get (get pic (+ y dy)) (+ x dx) b-c))
           offsets)))

(defn- binary->decimal [b]
  (Long/parseLong (apply str b) 2))

(defn- lookup [algo window]
  (algo (binary->decimal window)))

(defn- enhance [algo pic b-c]
  (let [size-y      (count pic)
        size-x      (count (first pic))
        points      (for [y (range size-y) x (range size-x)] [x y])
        init-output (vec (repeat size-y (vec (repeat size-x 0))))
        output      (reduce
                      (fn [output [x y :as pt]]
                        (let [window   (get-window pic pt b-c)
                              enhanced (lookup algo window)]
                          (update output y (fn [row] (assoc row x enhanced)))))
                      init-output
                      points)]
    [output (get (get output 0) 0)]))

(defn- enhance-n [n [pic bc algo]]
  (-> (iterate (fn [[pic bc]]
                 (enhance algo (add-border pic bc) bc))
               [pic bc])
       (nth n)
       first))

(defn- count-lit-pixels [pic]
  (->> pic flatten (remove #(= % 0)) count))

(defn day20-solution-part1 []
  (->> "input.day20.txt" parse-file (enhance-n 2) count-lit-pixels))

(defn day20-solution-part2 []
  (->> "input.day20.txt" parse-file (enhance-n 50) count-lit-pixels))
