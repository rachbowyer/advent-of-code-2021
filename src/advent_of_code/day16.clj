(ns advent-of-code.day16
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn- zero-pad [n]
  (str (str/join (repeat (- 4 (count n)) "0")) n))

(defn- hex-stream->binary-stream [n]
  (->> n
       (map (fn [e]  (-> e
                         str
                         (Integer/parseInt 16)
                         Integer/toBinaryString
                         zero-pad)))
       (str/join)
       vec))

(defn- binary-stream->decimal [b]
  (Long/parseLong (apply str b) 2))

(declare parse-packet)

(defn- parse-literal-payload [init-stream]
  (loop [[f & r] init-stream
         output ""]
    (let [stream     (drop 4 r)
          new-output (apply str output (take 4 r))]
      (if  (= f \0)
        {:content {:literal (binary-stream->decimal new-output)}
         :stream (vec stream)}
        (recur stream new-output)))))

(defn parse-bit-length-content [stream]
  (let [packet-length (-> stream (subvec 0 15) binary-stream->decimal)]
         (loop [s (subvec stream 15 (+ 15 packet-length))
                contents []]
           (if (empty? s)
             {:content contents
              :stream (subvec stream (+ 15 packet-length))}
             (let [{:keys [stream] :as parsed} (parse-packet s)]
               (recur stream (conj contents (dissoc parsed :stream))))))))

(defn parse-packet-count-content [stream]
  (let [packet-count (-> stream (subvec 0 11) binary-stream->decimal)]
     (loop [s (subvec stream 11)
            i 0
            contents []]
       (if (= i packet-count)
         {:content contents
          :stream s}
         (let [{:keys [stream] :as parsed} (parse-packet s)]
           (recur stream (inc i) (conj contents (dissoc parsed :stream))))))))

(defn- parse-packet [stream]
  (let [version (-> stream (subvec 0 3) binary-stream->decimal)
        type-id (-> stream (subvec 3 6) binary-stream->decimal)
        length-type (get stream 6)]
    (assoc
      (cond
        (= type-id 4)       (parse-literal-payload (subvec stream 6))
        (= length-type \0)  (parse-bit-length-content (subvec stream 7))
        (= length-type \1)  (parse-packet-count-content (subvec stream 7)))
      :type-id type-id
      :version version)))

(defn- version-sum [ast]
  (->> (:content ast) (map version-sum) (reduce + (or (:version ast) 0))))

(defn- decode-ast [{:keys [type-id content]}]
    (let [bool->int (fn [b] (if b 1 0))
          content   (or (:literal content) (map decode-ast content))]
      (case type-id
        4 content
        0 (reduce + content)
        1 (reduce * content)
        2 (apply min content)
        3 (apply max content)
        5 (bool->int (apply > content))
        6 (bool->int (apply < content))
        7 (bool->int (apply = content)))))

(defn parse-file [f]
  (-> f io/resource slurp hex-stream->binary-stream parse-packet))

(defn day16-solution-part1 []
  (-> "input.day16.txt" parse-file version-sum))

(defn day16-solution-part2 []
  (-> "input.day16.txt" parse-file decode-ast))