(ns advent-of-code.day19
  (:require [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (clojure.lang PersistentQueue)))

(defn- parse-line [line]
  (set (map (fn [e] (->> (str/split e #",")
                         (map #(Integer/parseInt %))))
            line)))

(defn- parse-file [filename]
  (->> filename io/resource slurp str/split-lines
       (partition-by #(str/starts-with? % "--"))
       (partition 2)
       (mapv (fn [[_ n]] (->> n (remove #(= % "")) parse-line)))))

(def ^:private id [[1 0 0 ] [0 1 0] [0 0 1]])

(def ^:private direction-transformations
  "These make the camera point along +ve z axis"
  [id

   ;; Rotate from +ve x to +ve z around y-axis
   [[0  0 -1]
    [0  1  0]
    [1  0  0]]

   ;; Rotate from -ve x to +ve z around y-axis
   [[ 0  0  1]
    [ 0  1  0]
    [-1  0  0]]

   ;; Rotate from -ve y to +ve z around x-axis
   [[1  0  0]
    [0  0 -1]
    [0  1  0]]

   ;; Rotate from +ve y to +ve z around x-axis
   [[1  0  0]
    [0  0  1]
    [0 -1  0]]

   ;; Rotate from -ve z to +ve z
   ;; Rotating around the y axis. Can be done around other axis
   ;; but we correct for this later
   [[-1  0  0]
    [ 0  1  0]
    [ 0  0 -1]]])

(defn- rotate-around-z-axis [theta]
  [[(int (Math/cos theta)) (int (- (Math/sin theta))) 0]
   [(int (Math/sin theta)) (int (Math/cos theta))     0]
   [0                0                                1]])

(def ^:private rotation-transformations
  [id
   (rotate-around-z-axis (- (/ Math/PI 2.0)))
   (rotate-around-z-axis Math/PI)
   (rotate-around-z-axis (/ Math/PI 2.0))])

(defn- mat-mult [matrix v]
  (mapv (fn [e] (reduce + (map * e v))) matrix))

(defn- transform [direction rotation sensor-output]
  (->> sensor-output
       (mat-mult rotation)
       (mat-mult direction)))

(defn- transform-sensor-output [direction rotation sensor-output]
  (map (partial transform direction rotation) sensor-output))

(defn- subtract-points [[x1 y1 z1] [x2 y2 z2]]
  [(- x1 x2) (- y1 y2) (- z1 z2)])

(defn- add-points [[x1 y1 z1] [x2 y2 z2]]
  [(+ x2 x1) (+ y2 y1) (+ z2 z1)])

(defn- offset-sensor-output [sensor-output offset]
  (mapv (partial add-points offset) sensor-output))

(defn- alignment-count [ocean-trench sensor-output point1 point2]
  (let [offset (subtract-points point1 point2)
        offset-output (offset-sensor-output sensor-output offset)]
    {:counts
     (->> offset-output
          (map (fn [e] (if (ocean-trench e) 1 0)))
          (reduce +))
     :offset offset
     :output offset-output}))

(defn- find-optimal-alignment [ocean-trench sensor-output]
  (let [alignment-counts
        (for [direction direction-transformations
              rotation rotation-transformations]
          (let [transformed-sensor-output
                (transform-sensor-output direction rotation sensor-output)]
            (for [a ocean-trench b transformed-sensor-output]
               (alignment-count ocean-trench transformed-sensor-output a b))))]
    (->> alignment-counts
         flatten
         (sort-by :counts)
         last)))

(defn- merge-sensor-output [ocean-trench sensor-output]
  (into ocean-trench sensor-output))

(defn- create-seafloor-map [ocean-trench sensor-outputs]
  (loop [queue                (into (PersistentQueue/EMPTY) sensor-outputs)
         mapped-ocean-trench  ocean-trench
         offsets              []]
    (let [head (peek queue)]
      (if (nil? head)
        {:mapped mapped-ocean-trench :offsets offsets}
        (let [{:keys [output counts offset]}
              (find-optimal-alignment mapped-ocean-trench head)]
          (if (>= counts 12)
            (recur (pop queue)
                   (merge-sensor-output mapped-ocean-trench output)
                   (conj offsets offset))
            (recur (conj (pop queue) head) mapped-ocean-trench offsets)))))))

(defn- manhattan-distance [[x1 y1 z1] [x2 y2 z2]]
  (+ (Math/abs (- x2 x1)) (Math/abs (- y2 y1)) (Math/abs (- z2 z1))))

(defn- largest-manhattan-distance [mapped-ocean-trench]
  (apply max (for [a mapped-ocean-trench b mapped-ocean-trench]
               (manhattan-distance a b))))

(defn day19-solution-part1 []
  (let [[f & r] (parse-file "input.day19.txt")]
    (-> (create-seafloor-map f r) :mapped count)))

(defn day19-solution-part2 []
  (let [[f & r] (parse-file "input.day19.txt")]
    (-> (create-seafloor-map f r) :offsets largest-manhattan-distance)))



