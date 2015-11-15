(ns notecards.core
  (:require [devtools.core :as devtools]
            [notecards.app :as app]
            [notecards.app-state :as app-state]
            [om.core :as om]))

(defonce setup (do
                 (devtools/set-pref! :install-sanity-hints true)
                 (devtools/install!)))

(let [content (aget (js/$ "#content") 0)]
  (if content
    (om/root
      app/app
      app-state/app-state
      {:target content})))
