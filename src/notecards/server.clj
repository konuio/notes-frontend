(ns notecards.server
    (:require [ring.util.response :as response]
              [compojure.core :refer [routes GET]]
              [compojure.route :as route]))

(def handler
  (routes
    (GET "/cards" [] (response/file-response "cards/index.html" {:root "resources/public"}))
    (route/not-found (response/file-response "index.html" {:root "resources/public"}))))
