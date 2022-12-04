(ns adventofcode2022.day04
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str])
  (:import [java.lang Integer]))

(defn parse-int [x] (Integer/parseInt x))

(def example-input "2-4,6-8
2-3,4-5
5-7,7-9
2-8,3-7
6-6,4-6
2-6,4-8")

(defn load-input []
  (slurp (io/resource "inputs/day04.txt")))

(defn parse-pair [s]
  (let [[min1 max1 min2 max2] (->> (re-matches #"(\d+)-(\d+),(\d+)-(\d+)" s)
                                   (drop 1)
                                   (map parse-int))]
    [{:low min1 :high max1}
     {:low min2 :high max2}]))

(defn parse-input [input]
  (->> (-> input (str/split #"\n"))
       (map parse-pair)))

(comment
  (parse-input example-input))

(defn fully-contains? [a b]
  (and (<= (:low a) (:low b))
       (>= (:high a) (:high b))))

(defn one-in-pair-fully-contained? [[a b :as _pair]]
  (or (fully-contains? a b)
      (fully-contains? b a)))

(defn count-fully-contained-pairs [pairs]
  (->> pairs
       (filter one-in-pair-fully-contained?)
       (count)))

;; part 2

(defn overlaps? [[a b :as _pair]]
  (or
   ;;    a   a
   ;;  b       b
   (and (<= (:low b) (:low a))
        (<= (:high a) (:high b)))

   ;;    a    a
   ;;  b    b
   (and (<= (:low b) (:low a))
        (<= (:low a) (:high b)))

   ;;    a    a
   ;;     b    b
   (and (<= (:low a) (:low b))
        (<= (:low b) (:high a)))))

(comment
  (overlaps? [{:low 10, :high 98} {:low 9, :high 99}]))

(defn count-overlapping-pairs [pairs]
  (->> pairs
       (filter overlaps?)
       (count)))

(comment
  ;; part 1
  (count-fully-contained-pairs (parse-input example-input)) ;; => 2
  (count-fully-contained-pairs (parse-input (load-input))) ;; => 305

  ;; part 2
  (count-overlapping-pairs (parse-input example-input)) ;; => 4
  (count-overlapping-pairs (parse-input (load-input))) ;; => 305
  )
