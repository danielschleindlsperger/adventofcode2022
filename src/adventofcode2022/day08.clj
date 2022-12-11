(ns adventofcode2022.day08
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [clojure.java.io :as io])
  (:import
   [java.lang Integer]))

(defn parse-int [x] (Integer/parseInt (str x)))

(def example-input "30373
25512
65332
33549
35390")

(defn load-input []
  (slurp (io/resource "inputs/day08.txt")))

(defn transpose [matrix] (apply map list matrix))

(defn parse-input [input]
  (->> (str/split-lines input)
       (map #(map parse-int %))
       ;; transpose to make x/y coordinate handling more intuitive
       (transpose)
       (map #(into [] %))
       (into [])))

(comment
  (parse-input example-input))

(defn cells
  "Returns all [x y] combinations for matrix m in a list."
  [m]
  (let [len (count m)]
    (for [y (range len)
          x (range len)]
      [x y])))

(defn cells-to-the-left [grid pos]
  (loop [[x y :as _cursor] pos
         cells []]
    (if (= x 0)
      cells
      (recur [(dec x) y]
             (conj cells [(dec x) y])))))

(defn cells-to-the-right [grid pos]
  (loop [[x y :as _cursor] pos
         cells []]
    (if (<= (dec (count grid)) x)
      cells
      (recur [(inc x) y]
             (conj cells [(inc x) y])))))

(defn cells-on-top [grid pos]
  (loop [[x y :as _cursor] pos
         cells []]
    (if (= 0 y)
      cells
      (recur [x (dec y)]
             (conj cells [x (dec y)])))))

(defn cells-below [grid pos]
  (loop [[x y :as _cursor] pos
         cells []]
    (if (<= (dec (count grid)) y)
      cells
      (recur [x (inc y)]
             (conj cells [x (inc y)])))))

(defn visible-from-outside? [grid pos]
  (let [value (get-in grid pos)]
    (or
     ;; visible from top
     (empty? (cells-on-top grid pos))
     (not-any? #(<= value (get-in grid %)) (cells-on-top grid pos))
     ;; visible from right
     (empty? (cells-to-the-right grid pos))
     (not-any? #(<= value (get-in grid %)) (cells-to-the-right grid pos))
     ;; visible from bottom
     (empty? (cells-below grid pos))
     (not-any? #(<= value (get-in grid %)) (cells-below grid pos))
     ;; visible from left 
     (empty? (cells-to-the-left grid pos))
     (not-any? #(<= value (get-in grid %)) (cells-to-the-left grid pos)))))

(defn count-visible-trees [grid]
  (->> grid
       (cells)
       (filter #(visible-from-outside? grid %))
       (count)))

(defn get-height [grid pos] (get-in grid pos))

(defn single-dir-scenic-score [grid pos neighbours]
  (let [height (get-height grid pos)
        neighbour-heights (map (partial get-height grid) neighbours)
        smaller-trees (take-while #(< % height) neighbour-heights)
        blocked? (not= (count neighbour-heights) (count smaller-trees))]
    (if blocked?
      (inc (count smaller-trees))
      (count smaller-trees))))

(defn scenic-score [grid pos]
  (->> [cells-on-top cells-to-the-right cells-below cells-to-the-left]
       (map #(% grid pos))
       (map #(single-dir-scenic-score grid pos %))
       (apply *)))

(defn highest-scenic-score [grid]
  (->> grid
       (cells)
       (map #(scenic-score grid %))
       (sort #(compare %2 %1))
       (first)))

(comment
  ;; part 1
  (count-visible-trees (parse-input example-input)) ;; => 21
  (time (count-visible-trees (parse-input (load-input)))) ;; => 1533

  ;; part 2
  (highest-scenic-score (parse-input example-input)) ;; => 8
  (time (highest-scenic-score (parse-input (load-input))))) ;; => 345744
