(ns ^:figwheel-always notes-frontend.dev
  (:require [notes-frontend.core]
            [notes-frontend.app-state :as app-state]
            [shodan.console :as console]))

(defonce setup-
         (swap! app-state/app-state (fn [state]
                                      (merge state {:konu-url "http://localhost:8080"}))))
