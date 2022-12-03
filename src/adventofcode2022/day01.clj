(ns adventofcode2022.day01
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str])
  (:import [java.lang Integer]))

(defn load-input []
  (slurp (io/resource "inputs/day01.txt")))

(defn parse-int [x] (Integer/parseInt x))

(defn parse-input [input]
  (let [inv (-> input (str/split #"\n\n"))]
    (map #(map parse-int (str/split % #"\n")) inv)))

(comment
  (parse-input (load-input)))

(defn most-calories-carried [elves-inventory & {:keys [last-x]}]
  (->> elves-inventory
       (map #(apply + %))
       (sort #(compare %2 %1))
       (take last-x)))

(comment
;; part 1
  (most-calories-carried (parse-input (load-input)) :last-x 1) ;; => 75622

  ;; part 2
  (apply + (most-calories-carried (parse-input (load-input)) :last-x 3)) ;; => 213159
  )
