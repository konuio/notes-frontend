(ns notecards.button
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [shodan.console :as console :include-macros true]
            [goog.string.format]
            [clojure.string]))

(defn button [{:keys [className iconClassName] :as data}]
  (let [enabled (get data :enabled true)]
    (om/component
      (html [:button
             {:type "button"
              :className (js/classNames "button" (and enabled "button--enabled") className)}
             [:i
              {:className (js/classNames "button-icon" "icon" iconClassName)}]]))))

(defcard
  devcard-create-button*
  (om-root button)
  {:className "devcards-button"
   :iconClassName "ion-plus"})

(defcard
  devcard-delete-button
  (om-root button)
  {:className "devcards-button"
   :iconClassName "ion-trash-b"})

(defcard
  devcard-save-enabled-button
  (om-root button)
  {:className "devcards-button"
   :enabled true
   :iconClassName "ion-checkmark"})

(defcard
  devcard-save-disabled-button
  (om-root button)
  {:className "devcards-button"
   :enabled false
   :iconClassName "ion-checkmark"})

(defcard
  devcard-cancel-button*
  (om-root button)
  {:className "devcards-button"
   :iconClassName "ion-close"})
