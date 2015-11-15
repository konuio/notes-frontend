(ns notecards.components.pages
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [notecards.components.login :refer [login]]))

(defn login-card [{:keys [className user]}]
  (om/component
    (html [:div
           {:className (js/classNames "loginCard" className)}
           (om/build login
                     user)])))

(defn login-page [{:keys [className user]}]
  (om/component
    (html [:div
           {:className (js/classNames "loginPage" className)}
           (om/build login-card
                     {:className "loginPage-card"
                      :user user})])))

#_(defcard devcard-login-page*
         (om-root login-page)
         {:className "devcards-loginPage"
          :user {:username ""
                 :password ""}})