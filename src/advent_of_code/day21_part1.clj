(ns advent-of-code.day21-part1)

(defmulti roll-dice :type)

(defmethod roll-dice :deterministic [{:keys [next-roll] :as dice}]
  [next-roll (update dice :next-roll (fn [e] (-> e inc (mod 100))))])

(def ^:private deterministic-dice
  {:type      :deterministic
   :next-roll 1})

(defn- triple-roll [init-dice]
  (nth (iterate (fn [[total dice]]
                  (let [[v new-dice] (roll-dice dice)]
                    [(+ total v) new-dice]))
                [0 init-dice])
       3))

(defn- next-position [current-position v]
  (inc (mod (+ current-position -1 v) 10)))

(defn- play-turn [[dice players player-to-move]]
  (let [[v new-dice] (triple-roll dice)]
    [new-dice
     (update players
             player-to-move
             (fn [{:keys [position score]}]
               (let [new-position (next-position position v)]
                 {:position new-position
                  :score (+ score new-position)})))
     (mod (inc player-to-move) 2)]))

(defn- play-game [player1-start player2-start]
  (let [[game result]
    (split-with (fn [[_ positions _]]
                  (every? #(< (:score %) 1000) positions))
                (iterate
                  play-turn
                  [deterministic-dice [{:position player1-start :score 0}
                                       {:position player2-start :score 0}]
                   0]))]
    [game (first result)]))

(defn- moves-times-loser [[game [_ positions _ :as result]]]
  (* 3 (count game) (apply min (map :score positions))))

(defn day21-solution-part1 []
  (->> [1 6] (apply play-game) moves-times-loser))

