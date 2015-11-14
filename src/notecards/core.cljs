(ns notecards.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [devtools.core :as devtools]
            [shodan.console :as console :include-macros true]
            ))

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(om/root
  (fn [data _]
    (reify om/IRender
      (render [_]
        (dom/h1 nil (:text data)))))
  app-state
  {:target (. js/document (getElementById "content"))})

(defonce setup (do
                 (devtools/set-pref! :install-sanity-hints true)
                 (devtools/install!)
                 ))

(console/log (range 200))

(defn on-jsload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
