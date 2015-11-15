(ns notecards.history
  (:require [shodan.console :as console]
            [secretary.core :as secretary :refer-macros [defroute]]
            [notecards.app-channel :as app-channel]
            [goog.history.EventType]
            [goog.history.Html5History]
            [goog.events]))

(defn define-routes! [ch]
  (defroute home-path "/" []
            (app-channel/post-set-page! ch :home))
  (defroute login-path "/login" []
            (app-channel/post-set-page! ch :login))
  (defroute signup-path "/signup" []
            (app-channel/post-set-page! ch :signup)))

(defonce history
  (doto (goog.history.Html5History.)
    (.setPathPrefix "")
    (.setUseFragment false)
    (goog.events/listen goog.history.EventType/NAVIGATE
                        (fn [e]
                          (console/log "url change:" e)
                          (secretary/dispatch! (.-token e))))
    (.setEnabled true)))