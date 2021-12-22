(ns advent-of-code.day16-test
  (:require
    [advent-of-code.day16 :as d16]
    [clojure.test :refer :all]))

(def ^:private example1 "D2FE28")
(def ^:private example2 "38006F45291200")
(def ^:private example3 "EE00D40C823060")
(def ^:private example4 "8A004A801A8002F478")
(def ^:private example5 "620080001611562C8802118E34")
(def ^:private example6 "C0015000016115A2E0802F182340")
(def ^:private example7 "A0016C880162017C3686B18A3D4780")

(def ^:private example1-response
  {:content {:literal 2021} :version 6})

(def ^:private example2-response
  {:content [{:content {:literal 10} :type-id 4, :version 6}
             {:content {:literal 20} :type-id 4, :version 2}]
   :version 1})

(def ^:private example3-response
  {:content [{:content {:literal 1}, :type-id 4, :version 2}
             {:content {:literal 2}, :type-id 4, :version 4}
             {:content {:literal 3}, :type-id 4, :version 1}]
   :version 7})

(def ^:private example4-response
 {:content [{:content [{:content [{:content {:literal 15},
                                   :type-id 4
                                   :version 6}]
                        :type-id 2,
                        :version 5}]
             :type-id 2
             :version 1}]
  :version 4})

(deftest parse-packet-test
  (are [hex expected] (= expected
                         (-> hex
                             (#'d16/hex-stream->binary-stream)
                             (#'d16/parse-packet)
                             (dissoc :stream :type-id )))
    example1  example1-response
    example2  example2-response
    example3  example3-response
    example4  example4-response))

(deftest version-sum-test
  (are [hex expected] (= expected
                         (-> hex
                             (#'d16/hex-stream->binary-stream)
                             (#'d16/parse-packet)
                             (#'d16/version-sum)))
  example4 16
  example5 12
  example6 23
  example7 31))

(deftest decode-ast-test
  (are [hex no] (= no
                   (-> hex
                       (#'d16/hex-stream->binary-stream)
                       (#'d16/parse-packet)
                       (#'d16/decode-ast)))
    "C200B40A82" 3
    "04005AC33890" 54
    "9C0141080250320F1802104A08" 1))
