(ns advent-of-code.day14
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- parse-rules [r]
  (into {} (map #(str/split % #" -> ") r)))

(defn- parse-file [file]
  (let [[t _ & r ] (-> file io/resource slurp str/split-lines)]
    [(parse-rules r) t]))

(defn- pairify [template]
  (frequencies (map str template (rest template))))

(defn- expand [rules freq]
  (->> freq
       (reduce (fn [acc [[f s :as p] v]]
                 (let [m (rules p)]
                   (merge-with + acc {p (- 0 v)} {(str f m) v} {(str m s) v})))
               freq)
       (remove (comp zero? second))
       (into {})))

(defn- ->character-frequencies [template freq]
  (->> freq
       (mapcat (fn [[[f s] v]] [[f v] [s v]]))
       (concat [[(first template) 1] [(last template) 1]])
       (reduce (fn [acc [k v]] (merge-with + acc {k v})) {})
       (map (fn [e] (update e 1 #(/ % 2))))
       (into {})))

(defn least-and-most-common [char-frequencies]
  (let [sorted-frequencies (sort-by second char-frequencies)]
    ((juxt (comp second last) (comp second first)) sorted-frequencies)))

(defn process-and-report [n rules template]
  (let [freq (nth (iterate (partial expand rules) (pairify template)) n)]
    (->> freq (->character-frequencies template) least-and-most-common (apply -))))

(defn day14-solution-part1 []
  (->> "input.day14.txt" parse-file (apply process-and-report 10)))

(defn day14-solution-part2 []
  (->> "input.day14.txt" parse-file (apply process-and-report 40)))