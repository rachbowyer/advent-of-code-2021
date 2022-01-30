(ns advent-of-code.day22-part2
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]
    [medley.core :refer [abs]]))

;; todo Need to ensure be x1 <= x2

(def ^:private regex
  #"(\w+) x=([-\d]+)..([-\d]+),y=([-\d]+)..([-\d]+),z=([-\d]+)..([-\d]+)")

(defn- parse-line [line]
  (->> line
       (re-find regex)
       rest
       (map (fn [e] (case e "on" 1 "off" -1 (Integer/parseInt e))))))

(defn- parse-lines [lines]
  (map (fn [e] (let [[f & r] (parse-line e)] [f r])) lines))

(defn- parse-file [filename]
  (->> filename io/resource slurp str/split-lines parse-lines))

(defn size-cuboid [[x1 x2 y1 y2 z1 z2]]
  (letfn [(len [a1 a2] (inc (abs (- a2 a1))))]
    (* (len x2 x1) (len y2 y1) (len z2 z1))))

(defn normalize [[x1 x2 :as co-ords]]
  (if (> x1 x2) [x2 x1] co-ords))

(defn intersect-axis [x1 x2 x1' x2']
  (let [[x1 x2] (normalize [x1 x2])
        [x1' x2'] (normalize [x1' x2'])
        lhs (max x1 x1')
        rhs (min x2 x2')]
    (when (>= rhs lhs)
      [lhs rhs])))

(defn intersection-cuboids
  [[x1 x2 y1 y2 z1 z2] [x1' x2' y1' y2' z1' z2']]
  (let [x-intersection (intersect-axis x1 x2 x1' x2')
        y-intersection (intersect-axis y1 y2 y1' y2')
        z-intersection (intersect-axis z1 z2 z1' z2')]
    (when (and x-intersection y-intersection z-intersection)
      (-> [x-intersection y-intersection z-intersection] flatten vec))))

(defn- add-cuboid [init-cuboids  [t2 c2 :as new-cuboid]]
  (loop [[[t1 c1 :as f] & r] init-cuboids
         output []]
    (if (nil? f)
      (cond-> (concat init-cuboids output)
              (pos? t2) (conj new-cuboid))
      (let [cuboid (intersection-cuboids c1 c2)
            intersection (when cuboid [(* t1 -1) cuboid])]
        (recur r (cond-> output intersection (conj intersection)))))))

(defn- reboot-reactor [commands]
  (reduce add-cuboid [] commands))

(defn- cubes-on [cuboids]
  (reduce (fn [acc [t c]]
            (+ acc (* (size-cuboid c) t)))
          0 cuboids))

(defn day22-solution-part2 []
  (-> "input.day22.txt" parse-file reboot-reactor cubes-on))

;;;; Strategy for adding cuboids

;;; if there is an on (+ve) cuboid added
;;; add the cuboid
;;; for the intersection between every new cuboid and existing list
;;; +ve and +ve => -nev
;;; -ve and +ve => +ve

;;; if there is an off (-ve) cuboid
;;; don't add the cuboid
;;; for the intersection between every new cuboid and existing list
;;; +ve and -ve => -ve
;;; -ve and -ve => +ve
