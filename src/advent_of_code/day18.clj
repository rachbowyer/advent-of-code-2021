(ns advent-of-code.day18
  (:require [clojure.zip :as z]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(defn- parse-file [file]
  (->> file io/resource slurp str/split-lines (map edn/read-string)))

(defn- pair? [n]
  (let [node (z/node n)]
    (and (seqable? node) (every? number? node))))

(defn- literal? [loc]
  (some-> loc z/node number?))

(defn- get-node
  "Next literal to the left or right of the node"
  [direction init-loc]
  (let [[down-and-move move]
        (if (= direction :left)
          [#(some-> % z/down z/right) z/left]
          [#(some-> % z/down) z/right])]
    (loop [loc init-loc
           moved false]
      (cond
        (or (nil? loc) (and moved (number? (z/node loc))))
        loc

        moved
        (recur (down-and-move loc) moved)

        :else
        (if-let [moved-loc (move loc)]
          (recur moved-loc true)
          (recur (z/up loc) false))))))

(defn- get-pair-to-explode
  "Leftmost pair that is 4 deep"
  ([loc] (get-pair-to-explode 1 loc))
  ([depth loc]
   (cond
     (and (pair? loc) (= depth 5))
     loc

     (or (> depth 5) (literal? loc))
     nil

     :else
     (let [left-child (z/down loc)]
       (if-let [target (get-pair-to-explode (inc depth) left-child)]
         target
         (recur (inc depth) (z/right left-child)))))))

(defn- update-node [loc direction v]
  (letfn [(get-flipped [l] (get-node (if (= direction :left) :right :left) l))]
    (if-let [node (get-node direction loc)]
      (-> node
          (z/replace (+ (z/node node) v))
          get-flipped)
      loc)))

(defn- explode [snailfish-num]
  (if-let [loc  (-> snailfish-num z/vector-zip get-pair-to-explode)]
    (let [[l r] (z/node loc)]
          [true (-> loc
                    (z/replace 0)
                    (update-node :left l)
                    (update-node :right r)
                    z/root)])
    [false snailfish-num]))

(defn- split [shellfish-num]
  (loop [loc        (z/vector-zip shellfish-num)
         have-split false]
    (if (z/end? loc)
      [have-split (z/root loc)]
      (let [[next-loc next-have-split]
        (if (and (literal? loc) (not have-split))
          (let [node (z/node loc)]
            (if (>= node 10)
              (let [divided-by-2 (/ node 2)]
                [(z/replace loc [(int (Math/floor divided-by-2))
                                 (int (Math/ceil divided-by-2))])
                 true])
              [loc have-split]))
            [loc have-split])]
        (recur (z/next next-loc) next-have-split)))))

(defn- reduce-shellfishnum [shellfish-num]
  (let [[exploded after-explode] (explode shellfish-num)]
    (if-not exploded
      (let [[been-split after-split] (split after-explode)]
        (if-not been-split
          after-split
          (recur after-split)))
      (recur after-explode))))

(defn- add-shellfish-num [x y]
  (reduce-shellfishnum [x y]))

(defn- add-list [l]
  (reduce add-shellfish-num l))

(defn- magnitude [shell-fish-number]
  (if (number? shell-fish-number)
    shell-fish-number
    (let [[l r] shell-fish-number]
      (+ (* 3 (magnitude l)) (* 2 (magnitude r))))))

(defn- largest-magnitude-shellfish-numbers [l]
  (apply max (for [x l y l :when (not= x y)]
               (magnitude (add-shellfish-num x y)))))

(defn day18-solution-part1 []
  (-> "input.day18.txt" parse-file add-list magnitude))

(defn day18-solution-part2 []
  (-> "input.day18.txt" parse-file largest-magnitude-shellfish-numbers))



;; I am a little disappointed with the size and complexity of my solution. Part
;; of this is due to the difficulties in navigating and mutating trees in
;; Clojure, a language that uses immutable datastructures. The other reason is
;; my inexperience using zippers.
;;
;; The `get-node` function can be written using z/prev and z/next taking into
;; account that these do a pre-order traversal. The `get-pair-to-explode`
;; function can also be written using z/prev and z/next remembering that zipper
;; datastructure contains the path to the root and therefore the height of the
;; tree can always be readily obtained.
