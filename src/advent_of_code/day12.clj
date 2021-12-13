(ns advent-of-code.day12
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- parse-file [file]
  (->> file io/resource slurp str/split-lines (map #(str/split % #"-"))))

(defn- text->adjacency-list [text]
  (reduce
    (fn [acc [a b]]
      (letfn [(add [ac from to]
                (update ac (keyword from) #(conj (or % #{}) (keyword to))))]
        (-> acc (add a b) (add b a))))
    {} text))

(defn- small-cave? [node]
  (-> node name first Character/isLowerCase))

(defn- get-successors [graph node]
  (graph node))

(defn- explore [graph allow-ssc path single-small-cave node]
  (let [path-set (into #{} path)
        new-path (cons node path)]
    (if (= node :end)
      [new-path]

      (let [[allowed new-scc]
            (if (and (small-cave? node) (path-set node))
              (cond
                (not allow-ssc)           [false nil]
                (= node :start)           [false nil]
                (nil? single-small-cave)  [true node]
                :else                     [false nil])
              [true single-small-cave])]
        (if (not allowed)
          []
          (let [successors (get-successors graph node)]
            (mapcat (fn [e] (explore graph allow-ssc new-path new-scc e))
                    successors)))))))

(defn- count-paths [allow-ssc graph]
  (count (explore graph allow-ssc '() nil :start)))

(defn day12-solution-part1 []
  (->> "input.day12.txt" parse-file text->adjacency-list
      (count-paths false)))

(defn day12-solution-part2 []
  (->> "input.day12.txt" parse-file text->adjacency-list
      (count-paths true)))

;;83475 - takes around 3 secs
