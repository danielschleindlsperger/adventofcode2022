(ns adventofcode2022.day02
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.set :as set]))

(def example-input "A Y\nB X\nC Z")

(defn load-input []
  (slurp (io/resource "inputs/day02.txt")))

(defn parse-round [round]
  (rest (re-matches #"(A|B|C) (X|Y|Z)" round)))

(defn parse-input [input]
  (let [rounds (-> input (str/split #"\n"))]
    (map parse-round rounds)))

(comment
  (parse-input example-input))

(defn ->shape [x] (get {"A" :rock, "B" :paper, "C" :scissors, "X" :rock, "Y" :paper, "Z" :scissors} x))
(def beaten-by {:rock :paper, :paper :scissors, :scissors :rock})
(def beats (set/map-invert beaten-by))
(def points-from-shape {:rock 1, :paper 2, :scissors 3})
(def points-from-result {:win 6, :draw 3, :lose 0})

(defn ->opponent-shape [round] (->shape (nth round 0)))
(defn ->own-shape [round] (->shape (nth round 1)))
(defn ->target-outcome [round]
  (case (nth round 1)
    "X" :lose
    "Y" :draw
    "Z" :win))

(defn result [own-shape opponent-shape]
  (cond
    (= (beaten-by own-shape) opponent-shape) :lose
    (= own-shape opponent-shape) :draw
    (= (beaten-by opponent-shape) own-shape) :win))

(defn points-in-round [round]
  (+ (points-from-shape (->own-shape round))
     (points-from-result (result (->own-shape round) (->opponent-shape round)))))

(defn points [rounds]
  (->> rounds
       (map points-in-round)
       (apply +)))

;; part 2
(defn choose-shape [round]
  (let [opponent-shape (->opponent-shape round)
        target-outcome (->target-outcome round)]

    (case target-outcome
      :win (beaten-by opponent-shape)
      :lose (beats opponent-shape)
      :draw opponent-shape)))

(defn points-to-plan [rounds]
  (->> rounds
       (map
        (fn [round]
          (let [own-shape (choose-shape round)]
            (+ (points-from-shape own-shape)
               (points-from-result (result own-shape (->opponent-shape round)))))))
      (apply +) 
       ))

(comment
  ;; part 1
  (points (parse-input example-input)) ;; => 15
  (points (parse-input (load-input))) ;; => 13052

  ;; part 2
  (points-to-plan (parse-input example-input)) ;; => 12
  (points-to-plan (parse-input (load-input))) ;; => 13693
  )
