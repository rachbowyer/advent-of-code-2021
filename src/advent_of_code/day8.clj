(ns advent-of-code.day8
  (:require
    [clojure.set :as set]
    [clojure.string :as str]
    [clojure.java.io :as io]))

(defn permutations
  "Takes a set, returns a list"
  [items]
  (cond
    (empty? items) '()
    (= 1 (count items)) [(into [] items)]
    :else (mapcat (fn [e] (for [t (permutations (disj items e))]
                            (conj t e))) items)))

(defn- parse-segment-patterns [patterns]
  (map (fn [e] (->> e (map (comp keyword str)) (into #{})))
        (str/split patterns #" " )))

(defn- parse-file [filename]
  (map #(-> % (str/split #" \| ") ((partial map parse-segment-patterns)))
       (->> filename io/resource slurp str/split-lines)))

(def seven-segment-display
  {#{:a :b :c :e :f :g}     0
   #{:c :f}                 1
   #{:a :c :d :e :g}        2
   #{:a :c :d :f :g}        3
   #{:b :c :d :f}           4
   #{:a :b :d :f :g}        5
   #{:a :b :d :e :f :g}     6
   #{:a :c :f}              7
   #{:a :b :c :d :e :f :g}  8
   #{:a :b :c :d :f :g}     9})

(def valid-segment-patterns
  (->> seven-segment-display (map first) (into #{})))

(def valid-circuits
  (reduce set/union #{} valid-segment-patterns))

(defn all-mappings []
  (->> valid-circuits
       permutations
       (map (fn [p] (into {} (map vector valid-circuits p))))))

(defn map-segment-pattern [mapping segment-pattern]
  (->> segment-pattern (map mapping) (into #{})))

(defn valid-mapping? [segment-patterns mapping]
  (every? (fn [sp] (->> sp (map-segment-pattern mapping) valid-segment-patterns))
          segment-patterns))

(defn find-mapping [segment-patterns]
  (let [matching-mappings
        (filter (partial valid-mapping? segment-patterns) (all-mappings))]
    (assert (= (count matching-mappings) 1))
    (first matching-mappings)))

(defn segment-pattern->number [mapping segment-pattern]
  (->> segment-pattern (map mapping) (into #{}) seven-segment-display))

(defn ->numbers [input-segment-patterns output-segment-patterns]
  (let [mapping (find-mapping input-segment-patterns)]
    (map (partial segment-pattern->number mapping) output-segment-patterns)))

(defn- display->decimal [display]
  (Integer/parseInt (apply str display)))

(defn day8-solution-part1 []
  (->> "input.day8.txt"
        parse-file
        (mapcat (partial apply ->numbers))
        (filter #{1 4 7 8})
        count))

;; 301

(defn day8-solution-part2 []
  (->> "input.day8.txt"
       parse-file
       (map (partial apply ->numbers))
       (map display->decimal)
       (reduce +)))

;; 908067

;; Simple brute force approach to the task. There are far more elegant
;; solutions using constraints and back tracking, but the brute force
;; approach runs in about 3 secs so I am happy.





