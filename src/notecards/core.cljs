(ns notecards.core
  (:require [devtools.core :as devtools]
            [notecards.api :as api]
            [notecards.app :as app]
            [notecards.app-state :as app-state]
            [notecards.history :as history]
            [notecards.components.button]
            [notecards.components.login]
            [notecards.components.notes]
            [notecards.components.pages]
            [om.core :as om]
            [shodan.console :as console]))

(defonce setup (do
                 (devtools/set-pref! :install-sanity-hints true)
                 (devtools/install!)))

(let [content (aget (js/$ "#content") 0)]
  (if content
    (om/root
      app/app
      app-state/app-state
      {:target content})))
