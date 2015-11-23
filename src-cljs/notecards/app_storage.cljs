(ns notecards.app-storage)

(def token-key "token")

(defn set-token [token]
  (.setItem js/localStorage token-key token))

(defn get-token []
  (.getItem js/localStorage token-key))