(ns advent-of-code.day24
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]))

(defn- parse-line [line]
  (mapv #(try (Integer/parseInt %)
              (catch NumberFormatException _ (keyword %)))
        (str/split line #" ")))

(defn- parse-file [file]
  (->> file io/resource slurp str/split-lines (mapv parse-line)))

(defn- div [a b] (int (/ a b)))

(def ^:private op->fn
  {:add +
   :mul *
   :div div
   :mod mod
   :eql #(if (= %1 %2) 1 0)})

(defn- alu
  [instructions initial-inputs]
  {:pre [(sequential? instructions) (sequential? initial-inputs)]}
  (loop [[[op operand-1 operand-2] & remaining-instructions] instructions
         registers {:w 0, :x 0, :y 0, :z 0}
         [next-input & remaining-input :as inputs] initial-inputs]
    (if (nil? op)
      registers
      (do
        (assert (keyword? op))
        (assert (keyword? operand-1))
        (if (= op :inp)
          (do
            (assert next-input)
            (recur remaining-instructions
                   (assoc registers operand-1 next-input)
                   remaining-input))
          (do
            (assert (or (keyword? operand-1) (number? operand-1)))
            (let [val1 (operand-1 registers)
                  val2 (if (keyword? operand-2)
                         (operand-2 registers)
                         operand-2)
                  result ((op->fn op) val1 val2)]
              (recur remaining-instructions
                     (assoc registers operand-1 result)
                     inputs))))))))

;; Example of running the alu
;(:z (alu (parse-file "input.day24.txt") [1 3 5 7 9 2 4 6 8 9 9 9 9 9]))

;; Rewrite of the check serial number program in Clojure

(def ^:private constants
  ;;a      b     c
  [[1     15    13]     ; 1
   [1     10    16]     ; 2
   [1     12    2]      ; 3
   [1     10    8]      ; 4
   [1     14    11]     ; 5
   [26   -11    6]      ; 6
   [1     10    12]     ; 7
   [26   -16    2]      ; 8
   [26   -9     2]      ; 9
   [1     11    15]     ; 10
   [26   -8     1]      ; 11
   [26   -8     10]     ; 12
   [26   -10    14]     ; 13
   [26   -9     10]])   ; 14

(defn- step [input-z [[a b c] input]]
  (if (= (- input b) (mod input-z 26))
    (div input-z a)
    (+ (* (div input-z a) 26) input c)))

(defn check-serial-number
  [serial-number]
  (reduce step 0 (map vector constants serial-number)))

;; Approach to find the highest valid serial numbers

;; Inputs are paired as followed
;; 1 and 14, 2 and 13, 3 and 12, 4 and 9 are paired
;; 5 and 6, 7 and 8, 10 and 11
;;
;; Initially set 1,2,3,4,5,7 and 10 to 9
;; Adjust the corresponding digit to ensue that the (div 26) takes place
;; If this is impossible drop the first digit from 9 towards 1 until
;; a (div 26) can take place in the corresponding digit

;; This gives
;; (check-serial-number [5 3 9 9 9 9 9 5 8 2 9 3 9 9])
;; (:z (alu (parse-file "input.day24.txt") [5 3 9 9 9 9 9 5 8 2 9 3 9 9]))
;; as the highest valid serial number

;; Part 2 is the reverse
;; (check-serial-number [1 1 7 2 1 1 5 1 1 1 8 1 7 5])




