(ns notecards.utils
  (:require [shodan.console :as console]))

(defn truncate [s n]
  (subs s 0 (min (count s) n)))

(defn find-first [pred seq]
  (first (filter pred seq)))

(defn find-index [pred seq]
  (let [[i _] (find-first (fn [[_ x]] (pred x)) (map-indexed vector seq))]
    i))
