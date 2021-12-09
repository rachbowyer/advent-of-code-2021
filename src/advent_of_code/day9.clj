(ns advent-of-code.day9
  (:require
    [clojure.string :as str]
    [clojure.java.io :as io]))

(defn parse-str [data]
  (mapv(fn [e] (mapv #(Integer/parseInt (str %)) e))
         (str/split-lines data)))

(defn- parse-file [filename]
  (->> filename io/resource slurp parse-str))

(def ^:private adjacent-offsets
  [[-1 0] [1 0] [0 -1] [0 1]])

(defn get-height [heights [x y]]
  (get (get heights y) x))

(defn get-x-max [heights]
  (count (get heights 0)))

(defn get-y-max [heights]
  (count heights ))

(defn get-neighbours [heights [x y]]
  (->> adjacent-offsets
       (map (fn [[x' y']] [(+ x x') (+ y y')]))
       (remove (fn [[x y]]
                 (or (neg? x) (neg? y)
                     (>= x (get-x-max heights))
                     (>= y (get-y-max heights)))))))

(defn local-minima? [heights co-ord]
  (every? #(< (get-height heights co-ord)
              (get-height heights %))
          (get-neighbours heights co-ord)))

(defn get-lowest-points [heights]
  (->> (for [y (range (get-y-max heights)) x
             (range (get-x-max heights))] [x y])
       (filter (partial local-minima? heights))))

(defn calc-risk [heights]
  (let [lowest-points (get-lowest-points heights)]
    (->> lowest-points
         (map (partial get-height heights))
         (reduce + (count lowest-points)))))

(defn get-basin [heights low-point]
  (loop [[n & r]  (list low-point)
         output   #{}]
    (if (nil? n)
      output
      (let [in-basin
            (->> (get-neighbours heights n)
                 (filter (fn [e] (> (get-height heights e)
                                    (get-height heights n))))
                 (remove #(= (get-height heights %) 9))
                 (remove output))]

        (recur (concat in-basin r)
               (conj output n))))))

(defn day8-solution-part1 []
  (->> "input.day9.txt" parse-file calc-risk))

;; 518

(defn day8-solution-part2 []
  (let [heights (parse-file "input.day9.txt")]
    (->> heights
         get-lowest-points
         (map (partial get-basin heights))
         (map count)
         (sort >)
         (take 3)
         (reduce *))))

;; 660480


