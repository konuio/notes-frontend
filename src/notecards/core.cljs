(ns notecards.core
  (:require [devtools.core :as devtools]
            [notecards.button]
            [notecards.page]))

(defonce setup (do
                 (devtools/set-pref! :install-sanity-hints true)
                 (devtools/install!)))

;; define your app data so that it doesn't get over-written on reload

;(defonce app-state (atom {:text "Hello world!"}))

#_(om/root
    (fn [data _]
      (reify om/IRender
        (render [_]
          (dom/h1 nil (:text data)))))
    app-state
    {:target (. js/document (getElementById "content"))})
