(ns advent_of_code.day2
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- load-data []
  (-> "input.day2.txt"
      io/resource
      slurp
      (str/split #"\n")
      ((partial map #(str/split % #" ")))
      ((partial map (fn [e] (update e 1 #(Integer/parseInt %)))))))

(defn day2-solution-part1 []
  (->> (load-data)
       (reduce
         (fn [acc [direction amount]]
           (apply update acc
               (case direction
                 "up" [:depth #(- % amount)]
                 "down" [:depth #(+ % amount)]
                 "forward" [:horizontal #(+ % amount)])))
         {:depth 0 :horizontal 0})
       (map second)
       (apply *)))

(defn day2-solution-part2 []
  (let [acc
        (reduce
          (fn [acc [direction amount]]
            (case direction
              "up"
              (update acc :aim #(- % amount))

              "down"
              (update acc :aim #(+ % amount))

              "forward"
              (-> acc
                  (update :horizontal #(+ % amount))
                  (update :depth #(+ % (* amount (:aim acc)))))))
          {:depth 0 :horizontal 0 :aim 0}
          (load-data))]

    (apply * ((juxt :depth :horizontal) acc))))

