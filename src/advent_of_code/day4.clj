(ns advent-of-code.day4
  (:require [clojure.set :as set]
            [clojure.java.io :as io]
            [clojure.string :as str]))

;; Day 4 - https://adventofcode.com/2021/day/4

;; Co-ordinate [x y], [0 0] origin top left
;; board - consists of a vector of horizontal vectors
;; marks - consists of a set of co-ordinates

(def ^:private board-width 5)

(defn- parse-board [lines]
  (vec (for [row (rest lines)]
         (as-> row $ (str/triml $)
               (str/split $ #"[ ]+")
               (map #(Integer/parseInt %) $)
               (vec $)))))

(defn- load-data [filename]
  (let [lines
        (->> filename io/resource slurp str/split-lines)

        drawn-number
        (-> lines first (str/split #",") ((partial map #(Integer/parseInt %))))

        boards
        (map parse-board (partition (inc board-width) (rest lines)))]

        [boards drawn-number]))

(def ^:private winning-rows
  (for [y (range board-width)] (for [x (range board-width)] [x y])))

(def ^:private winning-cols
  (for [x (range board-width)] (for [y (range board-width)] [x y])))

(def ^:private winning-marks
  (map set (concat winning-rows winning-cols)))

(defn- has-won? [marks]
  (some (fn [r] (set/subset? r marks)) winning-marks))

(defn- winning-position? [boards marks]
  (some (fn [[b m]] (and (has-won? m) [b m]))
        (map vector boards marks)))

(defn- get-number? [board [x y]]
  (-> board (get y) (get x)))

(defn- sum-unmarked-numbers [board marks]
  (reduce +
          (for [y (range board-width)
                x (range board-width)]
            (if-not (marks [x y]) (get-number? board [x y]) 0))))

(defn- apply-number [board drawn-number marks]
  (let [mark-location
        (first (for [y (range board-width)
                     x (range board-width)
                     :when (= (get-number? board [x y]) drawn-number)]
                 [x y]))]
    (cond-> marks mark-location (conj mark-location))))

(defn- play-bingo
  "Plays until there is a winner
   Returns (winning number, board, marks)"
  [boards drawn-numbers]
  (reduce
    (fn [marks drawn-number]
      (let [new-marks
            (map (fn [b m] (apply-number b drawn-number m)) boards marks)]
        (if-let [winning-position (winning-position? boards new-marks)]
          (reduced (cons drawn-number winning-position))
          new-marks)))
    (repeat (count boards) #{})
    drawn-numbers))

(defn- score-bingo [drawn-number board marks]
  (* drawn-number (sum-unmarked-numbers board marks)))

(defn play-and-score [boards drawn-numbers]
  (let [[drawn-number board marks] (play-bingo boards drawn-numbers)]
    (score-bingo drawn-number board marks)))

(defn day4-solution-part1 []
  (apply play-and-score (load-data "input.day4.txt")))

(defn remove-winning-boards [boards marks]
  (let [new-boards
        (remove #(has-won? (second %))
                (map vector boards marks))]
    [(map first new-boards) (map second new-boards)]))

(defn- last-board-to-win [init-boards init-drawn-numbers]
  (loop [drawn-numbers  init-drawn-numbers
         boards         init-boards
         marks          (repeat (count boards) #{})]
      (let [new-marks
            (map (fn [b m]
                   (apply-number b (first drawn-numbers) m)) boards marks)

            [new-boards new-new-marks]
            (remove-winning-boards boards new-marks)]
        (if (zero? (count new-boards))
          [(first drawn-numbers) (first boards) (first new-marks)]
          (recur (rest drawn-numbers) new-boards new-new-marks)))))

(defn- squid-win-and-score [boards drawn-numbers]
  (apply score-bingo (last-board-to-win boards drawn-numbers)))

(defn day4-solution-part2 []
  (apply squid-win-and-score (load-data "input.day4.txt")))

;; I don't like this solution as it is too long and complex. The extra complexity
;; comes from keeping the marks for each board, but there was no need to do
;; this.