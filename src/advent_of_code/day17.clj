(ns advent-of-code.day17)

(defn dec-to-zero [d]
  (if (pos? d) (dec d) d))

(defn simulate-path
  [[init-dx init-dy] [x-min-t x-max-t y-min-t y-max-t]]
  (loop [[x y] [0 0]
         y-max Float/NEGATIVE_INFINITY
         [dx dy] [init-dx init-dy]]
    (cond
      (and (<= x-min-t x x-max-t) (<= y-min-t y y-max-t))
      {:success true
       :y-max y-max
       :init-dx init-dx
       :init-dy init-dy}

      (or (> x x-max-t)
          (and (zero? dx)
               (or (not (<= x-min-t x x-max-t))
                   (< y y-min-t))))
      {:success false}

      :else
      (let [new-x (+ x dx)
            new-y (+ y dy)]
        (recur [new-x new-y] (max new-y y-max) [(dec-to-zero dx) (dec dy)])))))

(defn search-max-path [[_ x-max-t y-min-t _ :as target]]
  (->> (for [dx (range (inc x-max-t)) dy (range (min y-min-t 0) 1000)]
         (simulate-path [dx dy] target))
       (filter :success)))

(def ^:private target [96 125 -144 -98])

(defn day16-solution-part1 []
  (->> target search-max-path (sort-by :y-max) last :y-max))

(defn day16-solution-part2 []
  (->> target search-max-path count))

;; The maximum trick shot is found when dx is set to an initial velocity
;; that causes it to stall over the target area. dy is set to a high initial
;; velocity to obtain a large height, then the hope is that it hits the target
;; as it comes down. I have set a nominal limit of 1000 on dy, but I am not
;; sure there is an upper limit for dy.
;;
;; Reading other answers, it appears that when it returns to y=0, then
;; dy = - initial dy. This gives an upper bound on initial dy as
;; - y-min-t (if y-min-t < 0) as otherwise it will miss the
;; target area.
