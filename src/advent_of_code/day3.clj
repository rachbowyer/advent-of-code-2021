(ns advent-of-code.day3
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]))

(defn- load-data [filename]
  (->> filename io/resource slurp str/split-lines
       (mapv (fn [e] (mapv #(-> % str Integer/parseInt) e)))))

(def ^:private transpose
  (partial apply map vector))

(defn- ascii-array->bin [aa]
  (->> aa str/join (str "2r") read-string))

(defn- flip-ascii-array [aa]
  (map #(if (zero? %) 1 0) aa))

(defn- most-common-digit [row]
  (->> row (map #(if (zero? %) -1 1)) (reduce +)))

(defn- day3-solution-part1-impl [data]
  (let [common-bits
        (->> data transpose (map most-common-digit) (map #(if (pos? %) 1 0)))]
  (* (ascii-array->bin common-bits)
     (ascii-array->bin (flip-ascii-array common-bits)))))

(defn- calculate-rating [init-data predicate]
  (loop [data (map #(conj % %) init-data)]
    (if (= (count data) 1)
      (-> data first last ascii-array->bin)
      (let [first-column-filter (->> data transpose first most-common-digit)
            digit               (if (predicate first-column-filter) 1 0)
            filtered-data       (filter #(= (first %) digit) data)]
         (recur (map rest filtered-data))))))

(defn- day3-solution-part2-impl [data]
  (apply * (map (partial calculate-rating data) [(complement neg?) neg?])))

(defn day3-solution-part1 []
  (-> "input.day3.txt" load-data day3-solution-part1-impl))

;; 3687446

(defn day3-solution-part2 []
  (-> "input.day3.txt" load-data day3-solution-part2-impl))

;; 4406844