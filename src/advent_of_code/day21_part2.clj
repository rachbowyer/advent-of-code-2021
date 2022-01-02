(ns advent-of-code.day21-part2
  (:require [clojure.data.priority-map :refer [priority-map]]))

(def ^:private three-sided-dice [1 2 3])

(def ^:private three-rolls-distribution
  (->> (for [a three-sided-dice b three-sided-dice c three-sided-dice]
         (+ a b c))
         frequencies))

(defn- next-position [current-position v]
  (inc (mod (+ current-position -1 v) 10)))

(defn- play-turn [[players player-to-move] dice-roll]
  [(update players
           player-to-move
           (fn [{:keys [position score]}]
             (let [new-position (next-position position dice-roll)]
               {:position new-position
                :score (+ score new-position)})))
   (mod (inc player-to-move) 2)])

(defn- create-initial-state [player1-start player2-start]
  [[{:position player1-start :score 0}
    {:position player2-start :score 0}]
    0])

(defn- has-won? [[{player1-score :score} {player2-score :score}]]
  (when (or (>= player1-score 21) (>= player2-score 21))
    (if (> player1-score player2-score) 0 1)))

(defn- priority [[[{player1-score :score} {player2-score :score}] _]]
  (+ player1-score player2-score))

(defn- quantum-game [player1-start player2-start]
  (let [initial-state (create-initial-state player1-start player2-start)]
    (loop [state->frequency {initial-state 1}
           priority-queue   (priority-map initial-state (priority initial-state))
           wins             [0 0]]
      (let [[[players _ :as game-state] _] (peek priority-queue)]
        (if (nil? game-state)
          wins

          (if-let [player (has-won? players)]
            (recur (dissoc state->frequency game-state)
                   (pop priority-queue)
                   (update wins player #(+ % (state->frequency game-state))))

            (let [[new-state->frequency new-priority-queue]
                  (reduce
                    (fn [[state->frequency priority-queue] [dice-roll drf]]
                      (let [state (state->frequency game-state)
                            new-state (play-turn game-state dice-roll)]
                        [(update state->frequency
                                 new-state
                                 #(+ (or % 0) (* drf state)))
                         (cond-> priority-queue
                                 (not (state->frequency new-state))
                                 (conj [new-state (priority new-state)]))]))
                    [state->frequency
                     (pop priority-queue)]
                    three-rolls-distribution)]
              (recur (dissoc new-state->frequency game-state)
                     new-priority-queue
                     wins))))))))

(defn day21-solution-part2 []
  (->> [1 6] (apply quantum-game) first))
