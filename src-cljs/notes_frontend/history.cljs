(ns notes-frontend.history
  (:require [shodan.console :as console]
            [secretary.core :as secretary]
            [goog.history.EventType]
            [goog.history.Html5History]
            [goog.history.Html5History.TokenTransformer]
            [goog.events]))

; wtf
(defonce history
         (let [transformer (goog.history.Html5History.TokenTransformer.)]
           (set! (.. transformer -retrieveToken)
                 (fn [path-prefix location]
                   (str (.-pathname location) (.-search location))))
           (set! (.. transformer -createUrl)
                 (fn [token path-prefix location]
                   (str path-prefix token)))
           (doto (goog.history.Html5History. js/window transformer)
             (.setUseFragment false)
             (.setPathPrefix ""))))

(defn parse-route-and-listen-for-navigation! []
  (doto history
    (goog.events/listen goog.history.EventType/NAVIGATE
                        (fn [e]
                          (console/log "url change:" e)
                          (secretary/dispatch! (.-token e))))
    (.setEnabled true)))
