(ns adventofcode2022.day03
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.set :as set]))

(def example-input "vJrwpWtwJgWrhcsFMMfFFhFp
jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
PmmdzqPrVvPwwTWBwg
wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
ttgJtRGJQctTZtZT
CrZsJsPPZsGzwwsLwLmpwMDw")

(defn load-input []
  (slurp (io/resource "inputs/day03.txt")))

(defn parse-input [input]
  (-> input (str/split #"\n")))

(comment
  (parse-input example-input))

;; Actually we could just give a starting char and continue to increase it...
(defn ->char [n offset] (char (+ n offset)))
(defn ->priority [n offset] [(->char n offset) n])
(def lowercase-priorities (map #(->priority % 96) (range 1 27)))
(def uppercase-priorities (map #(->priority % 38) (range 27 53)))
(def priorities (apply hash-map (flatten (concat lowercase-priorities uppercase-priorities))))

(defn items-in-compartments [rucksack]
  (map set (split-at (/ (count rucksack) 2) rucksack)))
(defn items-in-rucksack [compartments]
  (apply set/union compartments))

(defn shared-items [compartments]
  (apply set/intersection compartments))

(defn shared-item-type-prio-sum [rucksacks]
  (->> rucksacks
       (map items-in-compartments)
       (map shared-items)
       (map first)
       (map priorities)
       (apply +)))

;; part 2
(defn rucksacks->groups [rucksacks] (partition 3 rucksacks))
(defn find-group-badge [group]
  (->> group
       (map items-in-rucksack)
       (apply set/intersection)
       first))

(defn group-badge-prio-sum [rucksacks]
  (->> rucksacks
       (map items-in-compartments)
       (rucksacks->groups)
       (map find-group-badge)
       (map priorities)
       (apply +)))

(comment
  ;; part 1
  (shared-item-type-prio-sum (parse-input example-input)) ;; => 157
  (shared-item-type-prio-sum (parse-input (load-input))) ;; => 7903

  ;; part 2
  (group-badge-prio-sum (parse-input example-input)) ;; => 70
  (group-badge-prio-sum (parse-input (load-input))) ;; => 2548
  )
