(ns notecards.devcards.navbar
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.components.buttons :refer [button]]))

(defcard navbar
         (om-root (fn [data]
                    (om/component
                      (html [:div.NavbarDevcards-navbar
                             (om/build
                               button
                               {:className "NavbarDevcards-button"
                                :content "Log out"})])))))
