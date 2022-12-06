(ns adventofcode2022.day06
  (:require
   [clojure.java.io :as io]))

(defn load-input []
  (slurp (io/resource "inputs/day06.txt")))

(defn find-start-of-packet-marker [input & {:keys [distinct-characters]}]
  (loop [signal (seq input)
         stack '()
         idx 0]
    (let [new-stack (conj stack (first signal))
          rem-signal (next signal)]
      (if (= distinct-characters (count (set (take distinct-characters new-stack))))
        {:stack (take distinct-characters new-stack) :idx idx}
        (recur rem-signal new-stack (inc idx))))))

(comment
  ;; part 1
  (find-start-of-packet-marker "mjqjpqmgbljsphdztnvjfqwrcgsmlb" :distinct-characters 4) ;; => 7
  (find-start-of-packet-marker "bvwbjplbgvbhsrlpgdmjqwftvncz" :distinct-characters 4) ;; => 5
  (find-start-of-packet-marker (load-input) :distinct-characters 4) ;; => 1542

  ;; part 2
  (find-start-of-packet-marker "mjqjpqmgbljsphdztnvjfqwrcgsmlb" :distinct-characters 14) ;; => 19
  (find-start-of-packet-marker "bvwbjplbgvbhsrlpgdmjqwftvncz" :distinct-characters 14) ;; => 23
  (find-start-of-packet-marker (load-input) :distinct-characters 14) ;; => 3152
  )
