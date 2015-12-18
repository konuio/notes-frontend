(ns ^:figwheel-always notes-frontend.prod
  (:require [notes-frontend.core]
            [notes-frontend.app-state :as app-state]))

(defonce setup-
  (swap! app-state/app-state (fn [state]
                               (merge state {:konu-url "https://api.konu.io"}))))
