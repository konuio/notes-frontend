(ns notes-frontend.history
  (:require [shodan.console :as console]
            [secretary.core :as secretary]
            [goog.history.EventType]
            [goog.history.Html5History]
            [goog.events]))

(defonce history
         (doto (goog.history.Html5History.)
           (.setPathPrefix "")
           (.setUseFragment false)))

(defn parse-route-and-listen-for-navigation! []
  (doto history
    (goog.events/listen goog.history.EventType/NAVIGATE
                        (fn [e]
                          (console/log "url change:" e)
                          (secretary/dispatch! (.-token e))))
    (.setEnabled true)))
