(ns adventofcode2022.day05
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str])
  (:import [java.lang Integer]))

(defn parse-int [x] (Integer/parseInt x))

(def example-input "    [D]    
[N] [C]    
[Z] [M] [P]
 1   2   3 

move 1 from 2 to 1
move 3 from 1 to 3
move 2 from 2 to 1
move 1 from 1 to 2")

(defn load-input []
  (slurp (io/resource "inputs/day05.txt")))

(defn parse-cmds [s]
  (map
   (fn [raw-cmd]
     (let [[qty from to] (->> (re-matches #"move (\d+) from (\d+) to (\d+)" raw-cmd)
                              (drop 1)
                              (map parse-int))]
       {:qty qty :from from :to to}))

   (str/split s #"\n")))

(defn transpose [matrix] (apply map list matrix))
(defn crate-column? [c]
  (contains? (set "0123456789") (first c)))
(defn parse-crate [crate]
  (let [stack-id (first crate)
        stack (reverse (filter #(not= % \space) (rest crate)))]
    {:stack-id (parse-int (str stack-id)) :stack stack}))

(defn parse-initial-stacks [s]
  (->> (str/split-lines s)
       transpose
       (map reverse)
       (filter crate-column?)
       (map parse-crate)))

(defn parse-input [input]
  (let [[raw-stacks raw-cmds] (str/split input #"\n\n")
        cmds (parse-cmds raw-cmds)
        initial-stacks (parse-initial-stacks raw-stacks)]
    {:cmds cmds
     :initial-stacks (into {} (for [stack initial-stacks] [(:stack-id stack) (:stack stack)]))}))

(comment
  (parse-input example-input)
  (parse-input (load-input)))

(defn move-cmd [stacks cmd & {:keys [one-by-one?]}]
  (let [moved-load (take (:qty cmd) (stacks (:from cmd)))
        updated-from-stack (drop (:qty cmd) (stacks (:from cmd)))
        ;; Ideally we'd just move the crates from one stack to the other one-by-one, but this works well enough
        updated-to-stack (concat (if one-by-one? (reverse moved-load) moved-load) (stacks (:to cmd)))]
    (-> stacks
        (assoc (:from cmd) updated-from-stack)
        (assoc (:to cmd) updated-to-stack))))

(defn work-crane [{:keys [cmds initial-stacks]} & {:keys [:one-by-one?]}]
  (reduce #(move-cmd %1 %2 :one-by-one? one-by-one?) initial-stacks cmds))

(defn top-crates [crates]
  (apply str (->> crates
                  (sort-by first)
                  (map second)
                  (map first))))

(comment
  ;; part 1
  (top-crates (work-crane (parse-input example-input) :one-by-one? true)) ;; => CMZ
  (top-crates (work-crane (parse-input (load-input)) :one-by-one? true)) ;; => QPJPLMNNR

  ;; part 2
  (top-crates (work-crane (parse-input example-input) :one-by-one? false)) ;; => MCD
  (top-crates (work-crane (parse-input (load-input)) :one-by-one? false)) ;; => BQDNWJPVJ
  )
