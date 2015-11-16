(ns notecards.cards.buttons
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.components.buttons :refer [button]]))

(defcard enabled
         (om-root button)
         {:className "ButtonCards-enabledPrimaryButton"
          :content "Button"})

(defcard disabled
         (om-root button)
         {:className "ButtonCards-disabledPrimaryButton"
          :content "Button"})

(defcard enabled-icon
         (om-root (fn [data]
                    (om/component
                      (om/build
                        button
                        {:className "ButtonCards-enabledPrimaryIconButton"
                         :content [:i.ButtonCards-icon.icon.ion-checkmark]})))))

(defcard disabled-icon
         (om-root (fn [data]
                    (om/component
                      (om/build
                        button
                        {:className "ButtonCards-disabledPrimaryIconButton"
                         :content [:i.ButtonCards-icon.icon.ion-checkmark]})))))

(defcard secondary
         (om-root button)
         {:className "ButtonCards-secondaryButton"
          :content "Button"})
