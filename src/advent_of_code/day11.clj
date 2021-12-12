(ns advent-of-code.day11
  (:require
    [clojure.string :as str]
    [clojure.java.io :as io]
    [clojure.set :as set]))

(defn- parse-file [filename]
  (->> filename io/resource slurp str/split-lines
       (mapv (fn [e] (mapv #(Integer/parseInt (str %)) e)))))

(defn- max-x [octopuses]
  (count (get octopuses 0)))

(defn- max-y [octopuses]
  (count octopuses))

(defn- get-octopus [octopuses [x y]]
  (get (get octopuses y) x))

(def ^:private neighbour-offsets
  [[-1 0] [1 0] [0 -1] [0 1] [-1 -1] [1 1] [-1 1] [1 -1]])

(defn- neighbours [max-x max-y [x y]]
  (->> neighbour-offsets
       (map (fn [[x' y']] [(+ x x') (+ y y')]))
       (remove (fn [[x y]] (or (neg? x) (neg? y) (>= x max-x) (>= y max-y))))))

(defn- zero-flashed [octopuses flashed]
  (mapv (fn [row y] (mapv (fn [e x] (if (flashed [x y]) 0 e)) row (range)))
        octopuses (range)))

(defn- apply-flashes
  [octopuses newly-flashed]
  (let [received-flash (->> newly-flashed
                            (mapcat (partial neighbours
                                             (max-x octopuses)
                                             (max-y octopuses)))
                            (group-by identity)
                            (map #(update % 1 count))
                            (into {}))]
    (mapv (fn [row y] (mapv (fn [e x] (+ e (or (received-flash [x y]) 0)))
                            row
                            (range)))
          octopuses (range))))

(defn- bump-one [octopuses]
  (mapv (fn [e] (mapv inc e)) octopuses))

(defn- one-cycle [[init-octopuses num-flashes step]]
  (loop [octopuses  (bump-one init-octopuses)
         flashed    #{}]
    (let [newly-flashed
          (->> (for [x (range (max-x octopuses))
                     y (range (max-y octopuses))]
                 [x y])
               (filter #(> (get-octopus octopuses %) 9))
               (remove flashed)
               (into #{}))]
      (if (empty? newly-flashed)
        [(zero-flashed octopuses flashed)
         (+ num-flashes (count flashed)) (inc step)]

        (recur
          (apply-flashes octopuses newly-flashed)
          (set/union flashed newly-flashed))))))

(defn- simulate-100-steps [octopuses]
  (as-> [octopuses 0 0] $
        (iterate one-cycle $)
        (nth $ 100)
        (second $)))

(defn- synchronised [octopuses]
  (every? (fn [row]
            (every? (fn [e] (= e (get-octopus octopuses [0 0]))) row))
          octopuses))

(defn- synchronised-point [octopuses]
  (let [not-sync (complement (comp synchronised first))]
    (nth (first (drop-while not-sync (iterate one-cycle [octopuses 0 0]))) 2)))

(defn day11-solution-part1 []
  (->> "input.day11.txt" parse-file simulate-100-steps))

(defn day11-solution-part2 []
  (->> "input.day11.txt" parse-file synchronised-point))