(ns notecards.devcards.buttons
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.components.buttons :refer [button]]))

(defcard enabled
         (om-root button)
         {:className "ButtonDevcards-enabledPrimaryButton"
          :content "Button"})

(defcard disabled
         (om-root button)
         {:className "ButtonDevcards-disabledPrimaryButton"
          :content "Button"})

(defcard enabled-icon
         (om-root (fn [data]
                    (om/component
                      (om/build
                        button
                        {:className "ButtonDevcards-enabledPrimaryIconButton"
                         :content [:i.ButtonDevcards-icon.icon.ion-checkmark]})))))

(defcard disabled-icon
         (om-root (fn [data]
                    (om/component
                      (om/build
                        button
                        {:className "ButtonDevcards-disabledPrimaryIconButton"
                         :content [:i.ButtonDevcards-icon.icon.ion-checkmark]})))))

(defcard secondary
         (om-root button)
         {:className "ButtonDevcards-secondaryButton"
          :content "Button"})
