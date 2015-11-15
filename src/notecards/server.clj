(ns notecards.server
    (:require [ring.util.response :as response]))

(defn handler []
      (response/file-response "index.html" {:root "public"}))