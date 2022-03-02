(ns advent-of-code.day23
  (:require
    [clojure.data.priority-map :refer [priority-map]]
    [clojure.set :as set]
    [medley.core :refer [abs]]))

;;;; Setup the Amphods burrow

;;; Origin (0,0) is the top left corner

(def ^:private step->energy
  {:a 1, :b 10, :c 100, :d 1000})

(def ^:private corridor-y 1)
(def ^:private upper-y 2)
(def ^:private lower-y 3)

(def ^:private side-rooms-x
  "x co-ord of each side room"
  {:a 3, :b 5, :c 7, :d 9})

(def ^:private side-rooms-y
  [upper-y lower-y])

(def ^:private corridor
  (->> (range 1 (inc 11)) (map #(vector % corridor-y)) set))

(def ^:private corridor-destinations
  (->> (vals side-rooms-x)
       (map #(vector % corridor-y))
       set
       (set/difference corridor)))

(defn- links-for-corridor
  []
  (reduce
    (fn [acc [x y :as pos]]
      (assoc acc pos
                 (cond-> []
                         (corridor [(dec x) y]) (conj [(dec x) y])
                         (corridor [(inc x) y]) (conj [(inc x) y]))))
    {}
    corridor))

(defn- links-for-side-rooms []
  (map (fn [x]
         {[x corridor-y]  [[x upper-y]]
          [x upper-y]     [[x corridor-y] [x lower-y]]
          [x lower-y]     [[x upper-y]]})
    (map val side-rooms-x)))

(defn- construct-burrow []
  (apply merge-with
         (comp vec concat)
         (links-for-corridor)
         (links-for-side-rooms)))

(def ^:private burrow
  "burrow using an adjacency map representation"
  (construct-burrow))


;;;; Parse starting positions

(def ^:private starting-position
  "Co-ord of each amphipod"
  {:a1 [(:c side-rooms-x) lower-y]
   :a2 [(:d side-rooms-x) upper-y]
   :b1 [(:b side-rooms-x) upper-y]
   :b2 [(:d side-rooms-x) lower-y]
   :c1 [(:a side-rooms-x) lower-y]
   :c2 [(:b side-rooms-x) lower-y]
   :d1 [(:a side-rooms-x) upper-y]
   :d2 [(:c side-rooms-x) upper-y]})

(def ^:private amphipods (keys starting-position))

;;;; Helpers

(defn- amphipod->genus [amphipod]
  (-> amphipod name first str keyword))

(defn- amphipod-goals
  "Return goal locations for amphipod - upper location first"
  [amphipod-genus]
  (for [y side-rooms-y] [(side-rooms-x amphipod-genus) y]))


;;;; Heuristic

;;; Can't be heuristic cannot overestimate the cost
;;; Must handle both in the side room
;;; 0 if in side room
;;; else get to the upper deck

(defn- manhattan-distance [[x1 y1] [x2 y2]]
  (+ (abs (- x2 x1)) (abs (- y2 y1))))

(defn heuristic-for-amphipod
  "If amphipod is in the correct side room than return 0
     amphipod - is an amphipod e.g. :a1
     position - is the amphipod's current position e.g. [3 3]"
  [amphipod current-position]
  (let [[upper-goal & _ :as goal-locations]
        (-> amphipod amphipod->genus amphipod-goals)
        distance (if ((set goal-locations) current-position)
                   0
                   (manhattan-distance current-position upper-goal))]
    (* (-> amphipod amphipod->genus step->energy)
       distance)))

(defn heuristic
  "positions: {:a1 -> [x y]}"
  [position]
  0
  (reduce (fn [acc amphipod]
            (+ acc (heuristic-for-amphipod amphipod (position amphipod))))
          0
          amphipods))


;;;; Work out available moves

(defn- get-blocked-locations
  [positions]
  (set (map val positions)))

(defn- cost-move-impl
  "Cost to move from current position to the destination
     blocked-locations - locations with an amiphod
     destination - destination co-ords
     cost - cumulative cost to reach current pos
     visited - set of points that has been visited
     path - path to the current point (included for debugging)"
  [amphipod-genus blocked-locations destination cost visited path current-pos]
  (cond
    (or (visited current-pos) (blocked-locations current-pos))
    nil

    (= current-pos destination)
    {:cost cost, :path path}

    :else
    (let [new-visited
          (conj visited current-pos)

          new-path
          (cons current-pos path)

          result
          (->> (burrow current-pos)
               (map (partial cost-move-impl
                             amphipod-genus
                             blocked-locations
                             destination
                             cost
                             new-visited
                             new-path))
               (remove nil?)
               first)]
      (some-> result (update :cost + (step->energy amphipod-genus))))))

(defn- cost-move
  [blocked-locations amphipod-genus current-pos destination]
  (cost-move-impl amphipod-genus
                  (disj blocked-locations current-pos)
                  destination
                  0 #{} []
                  current-pos))

(defn- get-valid-destinations
  "List of valid destinations given where the amphipod currently is.
   May not be able to make it to the destination if blocked

   Note an amiphod might be able to go straight from an incorrect side room
   to its goal location - this would be modelled as two moves"
  [reverse-position amphipod-genus [x y :as current-pos]]
  (let [[upper-goal lower-goal :as goals]
        (amphipod-goals amphipod-genus)

        goals-set
        (set goals)

        empty-side-room
        (every? (comp nil? reverse-position) goals)

        empty-upper
        (and (nil? (reverse-position upper-goal))
             (= (some-> lower-goal reverse-position amphipod->genus)
                amphipod-genus))]

    (cond
      ;; Has it reached its goal?
      ;; Need to make sure it is in correct side room and not blocking
      ;; another amphipod unless the same genus
      (and (goals-set current-pos)
           (or (not= y upper-y)
               (= (some-> [x lower-y] reverse-position amphipod->genus)
                  amphipod-genus)))
      []

      ;; If in corridor can then only go into side room if empty or the
      ;; lower is occupied by the same genus
      (and (corridor current-pos) empty-side-room)
      [lower-goal]

      (and (corridor current-pos) empty-upper)
      [upper-goal]

      ;; Must be in the starting side room and need to move to the
      ;; corridor
      (not (corridor current-pos))
      (vec corridor-destinations)

      :else
      [])))

(defn- reverse-position
  [position]
  (->> position (map (fn [[amphipod position]] [position amphipod])) (into {})))

(defn get-moves
  "Get all possible moves from this position
   Returns - list [:amphipod destination cost heuristic]"
  [position]
  (let [reversed-position (reverse-position position)
        blocked-locations (get-blocked-locations position)]
    (mapcat
      (fn [amphipod]
        (let [genus       (amphipod->genus amphipod)
              current-pos (position amphipod)
              destinations
              (get-valid-destinations reversed-position genus current-pos)]
          (->> destinations
               (map (fn [destination]
                      (let [new-position
                            (assoc position amphipod destination)

                            heuristic
                            (heuristic new-position)

                            cost
                            (cost-move blocked-locations
                                       genus
                                       current-pos
                                       destination)]
                        (some-> cost
                                (dissoc :path)
                                (assoc :destination destination
                                       :position new-position
                                       :amphipod amphipod
                                       :heuristic heuristic)))))
               (remove nil?))))
      amphipods)))


;;;; A* search

(defn succeeded?
  [position]
  (every?
    (fn [[amphipod [x y]]]
      (and ((set side-rooms-y) y)
           (= x (-> amphipod amphipod->genus side-rooms-x))))
    position))

(defn- get-lowest-cost
  "Queue node consists of [[position cost path] cost+heuristic]
   Returns
    Total cost
    Sequence of moves
    Number of nodes expanded"
  [starting-position]
  (loop [i 0
         explored #{starting-position}
         priority-queue (priority-map [starting-position 0 []]
                                      (heuristic starting-position))]
    (let [[[position current-cost path] _] (peek priority-queue)]
      (if (succeeded? position)
        [i current-cost path]
        (let [new-queue
              (->> (get-moves position)
                   (remove (conj explored :position))
                   (map (fn [{:keys [position cost amphipod destination heuristic]}]
                          (let [new-cost (+ current-cost cost)]
                            [[position
                              new-cost
                              (conj path [amphipod destination])]
                             (+ new-cost heuristic)])))
                   (reduce conj (pop priority-queue)))]
            (recur (inc i) (conj explored position) new-queue))))))


;(time (get-lowest-cost starting-position))
;"Elapsed time: 171564.41 msecs"
;=>
;[1374729
; 14460
; [[:a2 [2 1]]
;  [:d2 [8 1]]
;  [:b2 [10 1]]
;  [:d1 [6 1]]
;  [:d2 [9 3]]
;  [:d1 [9 2]]
;  [:c1 [6 1]]
;  [:a1 [8 1]]
;  [:c1 [7 3]]
;  [:a1 [3 3]]
;  [:a2 [3 2]]
;  [:b1 [4 1]]
;  [:c2 [6 1]]
;  [:c2 [7 2]]
;  [:b1 [5 3]]
;  [:b2 [5 2]]]]

;; Clojure is not really the language for these algorithmic challenges.
;; Having said that, there are plenty of ways to optimise the code -
;; including not bothering to store the path taken
;;
;; Second part is more of the same so skipped it

