(ns advent-of-code.day13
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))
(defn fold-paper [axis p points]
  (->> points
       (map (fn [point] (update point axis #(if (> % p) (- (* 2 p) %) %))))
       (into #{})))

(def ^:private fold-along-x (partial fold-paper 0))
(def ^:private fold-along-y (partial fold-paper 1))

(defn apply-folds [points operations]
  (reduce (fn [acc e] (e acc)) points operations))

(defn- split-points-command [data]
  (str/split data #"\n\n"))

(defn- parse-points [points]
  (->> points
       str/split-lines
       (map (fn [row] (mapv #(Integer/parseInt %) (str/split row #","))))
       (into #{})))

(defn- parse-commands [commands]
  (->> commands
       str/split-lines
       (map (fn [e] (let [[_ axis val] (re-matches #"^fold along (.)=(.*)$" e)
                         int-val (Integer/parseInt val)]
                      (case axis
                        "x" (partial fold-along-x int-val)
                        "y" (partial fold-along-y int-val)))))))

(defn parse-file [filename]
  (let [[points commands] (-> filename io/resource slurp split-points-command)]
    [(parse-points points)
     (parse-commands commands)]))

(defn- plot-points [points]
  (let [max-x (inc (apply max (map first points)))
        max-y (inc (apply max (map second points)))
        data (vec (repeat max-y (vec (repeat max-x "."))))

        plot (reduce (fn [acc [x y]] (update acc y (fn [v] (assoc v x "#"))))
                     data points)]
    (doseq [row plot]
      (doseq [point row]
        (print point))
      (println))))

(defn day14-solution-part1 []
  (as-> "input.day13.txt" $
        (parse-file $)
        (update $ 1 #(take 1 %))
        (apply apply-folds $)
        (count $)))

(defn day14-solution-part2 []
  (->> "input.day13.txt"
        parse-file
       (apply apply-folds)
        plot-points))

;; JRZBLGKH