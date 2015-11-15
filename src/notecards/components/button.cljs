(ns notecards.components.button
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [shodan.console :as console :include-macros true]
            [goog.string.format]
            [clojure.string]))

(defn button [{:keys [type className title iconClassName on-click] :as data}]
  (let [enabled (get data :enabled true)]
    (om/component
      (html [:button
             {:type "button"
              :className (js/classNames "button" (goog.string.format "button--%s" type) (if enabled "button--enabled") className)
              :on-click (if enabled on-click)}
             (if iconClassName
               [:i
                {:className (js/classNames "button-icon" "icon" iconClassName)}])
             (if title
               [:div.button-title title])]))))
(comment
(defcard
  devcard-login-button*
  (om-root button)
  {:className "devcards-loginButton"
   :title "Log In"})

(defcard
  devcard-signup-button
  (om-root button)
  {:className "devcards-signupButton"
   :title "Sign Up"
   :type "transparent"})

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
)