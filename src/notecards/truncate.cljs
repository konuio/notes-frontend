(ns notecards.truncate)

(defn truncate [s n]
  (subs s 0 (min (count s) n)))