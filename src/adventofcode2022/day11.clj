(ns adventofcode2022.day11
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [clojure.java.io :as io])
  (:import
   [java.lang Integer Math]))

(defn parse-int [x] (Integer/parseInt (str x)))

(def example-input "Monkey 0:
  Starting items: 79, 98
  Operation: new = old * 19
  Test: divisible by 23
    If true: throw to monkey 2
    If false: throw to monkey 3

Monkey 1:
  Starting items: 54, 65, 75, 74
  Operation: new = old + 6
  Test: divisible by 19
    If true: throw to monkey 2
    If false: throw to monkey 0

Monkey 2:
  Starting items: 79, 60, 97
  Operation: new = old * old
  Test: divisible by 13
    If true: throw to monkey 1
    If false: throw to monkey 3

Monkey 3:
  Starting items: 74
  Operation: new = old + 3
  Test: divisible by 17
    If true: throw to monkey 0
    If false: throw to monkey 1
")

(defn load-input []
  (slurp (io/resource "inputs/day11.txt")))

(defn num-or-variable [s]
  (try (parse-int s)
       (catch Exception _e s)))

(defn parse-worry-operation [raw-str]
  (let [[_ left op right] (re-matches #"(old|\d+) (\+|\*) (old|\d+)" raw-str)]
    {:left (num-or-variable left) :op op :right (num-or-variable right)}))

(comment
  (parse-worry-operation "old * 19")
  (parse-worry-operation "old + 6")
  (parse-worry-operation "old * old"))

(defn parse-monkey [raw-monkey]
  (let [[_ monkey-id]      (re-find #"Monkey (\d):"                     raw-monkey)
        [_ starting-items] (re-find #"Starting items: (.*)\n"           raw-monkey)
        [_ operation]      (re-find #"Operation: new = (.*)\n"          raw-monkey)
        [_ divisible-by]   (re-find #"divisible by (\d+)\n"             raw-monkey)
        [_ on-true]        (re-find #"If true: throw to monkey (\d+)\n" raw-monkey)
        [_ on-false]       (re-find #"If false: throw to monkey (\d+)"  raw-monkey)]
    {:monkey-id monkey-id
     :inspected-items 0
     :items (map parse-int (str/split starting-items #", "))
     :worry-operation  (parse-worry-operation operation)
     :throw-to {:divisible-by (parse-int divisible-by)
                :on-true on-true
                :on-false on-false}}))

(defn parse-input [input]
  (->> (str/split input #"\n\n")
       (map parse-monkey)
       (map (fn [monkey] [(:monkey-id monkey) monkey]))
       (into {})))

(comment
  (parse-input example-input)
  (parse-input (load-input)))

(def ops {"+" + "*" *})
(defn sub-old [op old-value]
  (if (= op "old") old-value op))

(defn worry [worry-level {:keys [op left right]}]
  ((ops op) (sub-old left worry-level) (sub-old right worry-level)))

(comment
  (worry 42 {:left "old", :op "+", :right 3})
  (worry 42 {:left "old", :op "*", :right "old"}))

(defn relieve [worry-level]
  (int (Math/floor (/ worry-level 3))))

(comment
  (relieve 81)
  (relieve 98))

(defn divisible? [throw-config worry-level]
  (= 0 (rem worry-level (:divisible-by throw-config))))

(defn throw-item [monkeys worry-level throw-config]
  (let [target (if (divisible? throw-config worry-level)
                 (:on-true throw-config)
                 (:on-false throw-config))]
    (update-in monkeys [target :items] conj worry-level)))

(defn play [monkeys current-monkey-id]
  (loop [monkey-state monkeys]
    (if (empty? (get-in monkey-state [current-monkey-id :items]))
      monkey-state
      (let [{:keys [worry-operation items throw-to] :as _monkey} (get monkey-state current-monkey-id)
            [curr-item & remaining] items
            worry-level (-> curr-item
                            (worry worry-operation)
                            (relieve))]
        (recur (-> monkey-state
                   (throw-item worry-level throw-to)
                   (assoc-in [current-monkey-id :items] remaining)
                   (update-in [current-monkey-id :inspected-items] inc)))))))

(defn playing-order [monkeys]
  (->> monkeys
       (vals)
       (map :monkey-id)
       (sort)))

(defn play-round [monkeys]
  (reduce play
          monkeys
          (playing-order monkeys)))

(comment
  (play-round (parse-input example-input)))

(defn play-n-rounds [monkeys n]
  (reduce (fn [state _] (play-round state))
          monkeys
          (range n)))

(comment
  (play-n-rounds (parse-input example-input) 20))

(defn monkey-business [monkeys n]
  (->> (play-n-rounds monkeys n)
       (vals)
       (map :inspected-items)
       (sort #(compare %2 %1))
       (take 2)
       (apply *)))

(comment
  ;; part 1
  (monkey-business (parse-input example-input) 20) ;; => 10605
  (monkey-business (parse-input (load-input)) 20) ;; => 151312

  ;; part 2
  (monkey-business (parse-input example-input) 10000) ;; => 10605
  )

