(ns advent-of-code.day10-test
  (:require
    [advent-of-code.day10 :as d10]
    [clojure.test :refer :all]))

(def ^:private example
  ["[({(<(())[]>[[{[]{<()<>>"
   "[(()[<>])]({[<{<<[]>>("
   "{([(<{}[<>[]}>{[]{[(<()>"
   "(((({<>}<{<{<>}{[]{[]{}"
   "[[<[([]))<([[{}[[()]]]"
   "[{[{({}]{}}([{[{{{}}([]"
   "{<[[]]>}<{[{[{[]{()[[[]"
   "[<(<(<(<{}))><([]([]()"
   "<{([([[(<>()){}]>(<<{{"
   "<{([{{}}[<[[[<>{}]]]>[]]"])

(deftest check-syntax-test
  (are [line result] (= result
                        (#'d10/check-syntax line))
     "{}" [:ok]
     "{"  [:incomplete "}"]
     "}"  [:syntax-error \}]
     "[{}{}]" [:ok]
     "{([(<{}[<>[]}>{[]{[(<()>" [:syntax-error \}]
     "[[<[([]))<([[{}[[()]]]"   [:syntax-error \)]
     "[{[{({}]{}}([{[{{{}}([]"  [:syntax-error \]]
     "[<(<(<(<{}))><([]([]()"   [:syntax-error \)]
     "<{([([[(<>()){}]>(<<{{"   [:syntax-error \>]
     "[({(<(())[]>[[{[]{<()<>>" [:incomplete "}}]])})]"]
     "[(()[<>])]({[<{<<[]>>("   [:incomplete ")}>]})"]
     "(((({<>}<{<{<>}{[]{[]{}"  [:incomplete "}}>}>))))"]
     "{<[[]]>}<{[{[{[]{()[[[]"  [:incomplete "]]}}]}]}>"]
     "<{([{{}}[<[[[<>{}]]]>[]]" [:incomplete "])}>"]))

(deftest score-syntax-errors-test
  (is (= 26397 (#'d10/score-syntax-errors example))))

(deftest score-autocomplete-requirements-test
  (are [completions result]
    (= result
       (#'d10/score-autocomplete-requirements completions))
    "}}]])})]"    288957
    ")}>]})"      5566
    "}}>}>))))"   1480781
    "]]}}]}]}>"   995444
    "])}>"        294))

(deftest score-autocomplete-block
  (is (= 288957 (#'d10/score-autocomplete-block example))))

