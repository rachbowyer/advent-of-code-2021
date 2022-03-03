(ns advent-of-code.day25
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- parse-line
  [y line]
  (map-indexed (fn [x e] [[x y] e]) line))

(defn- parse-file [filename]
  (let [data (->> filename io/resource slurp str/split-lines)]
    {:max-x (-> data first count)
     :max-y (count data)
     :data  (->> data (mapcat parse-line (range)) (into {}))}))

(defn- get-next-pos
  [max-x max-y next-move [x y]]
  (if (= next-move \>)
    [(mod (inc x) max-x) y]
    [x (mod (inc y) max-y)]))

(defn- move-one-herd
  [herd {:keys [data max-x max-y] :as init-seacumbers}]
  (loop [[[co-ord v :as f] & r]  data
         output   data
         moved    false]
    (if (nil? f)
      (assoc init-seacumbers
        :data output
        :moved moved)
      (let [next-spot (get-next-pos max-x max-y herd co-ord)]
        (if (and (= herd v) (= (data next-spot) \.))
          (recur r (assoc output co-ord \. next-spot herd) true)
          (recur r output moved))))))

(defn- single-step-seacumbers
  [sea-cumbers]
  (let [{east-moved :moved :as east-herd} (move-one-herd \> sea-cumbers)
        {south-moved :moved :as south-herd} (move-one-herd \v east-herd)]
    (assoc south-herd :moved (or east-moved south-moved))))

(defn move-seacucumbers
  [seacumbers]
  (->> (assoc seacumbers :next-move \> :moved true)
       (iterate single-step-seacumbers)
       (take-while :moved)
       count))

(defn day25-solution-part1 []
  (->> "input.day25.txt" parse-file move-seacucumbers))

;(defn display-seacumbers
;  [{:keys [data max-x max-y]}]
;  (doseq [y (range max-y)]
;    (doseq [x (range max-x)]
;      (print (data [x y])))
;    (println)))

