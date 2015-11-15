(ns notecards.server
    (:require [ring.util.response :as response]))

(defn handler [request]
      (response/file-response "index.html" {:root "resources/public"}))