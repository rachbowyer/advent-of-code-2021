(ns advent-of-code.day10
  (:require
    [clojure.string :as str]
    [clojure.java.io :as io]))

(defn- parse-file [filename]
  (-> filename io/resource slurp str/split-lines))

;; Rosetta stone version of median

(defn- median [ns]
  (let [ns (sort ns)
        cnt (count ns)
        mid (bit-shift-right cnt 1)]
    (if (odd? cnt)
      (nth ns mid)
      (/ (+ (nth ns mid) (nth ns (dec mid))) 2))))

(def ^:private pairs-of-braces
  {\{ \}
   \( \)
   \[ \]
   \< \>})

(def ^:private open-braces
  (->> pairs-of-braces (map first) (into #{})))

(def ^:private syntax-error-score
  { \)  3
    \] 57
    \} 1197
    \> 25137})

(def ^:private auto-complete-score
  {\) 1
   \] 2
   \} 3
   \> 4})

(defn- check-syntax [line]
  (loop [[f & r]                      line
         [fs & rs :as syntax-stack]   '()]
    (cond
      (and (nil? f) (nil? syntax-stack))
      [:ok]

      (nil? f)
      [:incomplete (apply str syntax-stack)]

      (and (not (open-braces f)) (not= f fs))
      [:syntax-error f]

      :else
      (if (open-braces f)
        (recur r (cons (pairs-of-braces f) syntax-stack))
        (recur r rs)))))

(defn score-syntax-errors [lines]
  (->> lines
       (map check-syntax)
       (filter (fn [[e ]] (= e :syntax-error)))
       (map (comp syntax-error-score second))
       (reduce +)))

(defn score-autocomplete-requirements [requirements]
  (reduce (fn [acc e] (+ (* acc 5) (auto-complete-score e)))
          0
          requirements))

(defn score-autocomplete-block [lines]
  (->> lines
       (map check-syntax)
       (filter (fn [[e ]] (= e :incomplete)))
       (map (comp score-autocomplete-requirements second))
       median))

(defn day10-solution-part1 []
  (->> "input.day10.txt" parse-file score-syntax-errors))

; 358737

(defn day10-solution-part2 []
  (->> "input.day10.txt" parse-file score-autocomplete-block))




