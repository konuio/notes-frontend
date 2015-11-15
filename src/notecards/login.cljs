(ns notecards.login
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.app-state :as app-state]))

(defn login-page [{:keys [className ch]}]
  (om/component
    (html [:div
           {:className (js/classNames "LoginPage" className)}
           [:form.LoginPage-card
            [:div.LoginPage-field
             [:label.LoginPage-label "Email"]
             [:input.LoginPage-input {:type "text"
                                      :defaultValue "mking@example.com"}]]
            [:div.LoginPage-field
             [:label.LoginPage-label "Password"]
             [:input.LoginPage-input {:type "password"
                                      :defaultValue "password"}]]
            [:div.LoginPage-actions
             [:button.LoginPage-login.LoginPage-action
              {:type     "submit"
               :on-click (fn [e]
                           (app-state/post-message! ch {:action :log-in
                                                        :username "foo"
                                                        :password "foo"}))}
              "Log in"]
             [:a.LoginPage-signup.LoginPage-action
              {:href     (routes/signup-path)
               :on-click (fn [e]
                           (.preventDefault e)
                           (.setToken history/history (routes/signup-path)))}
              "Sign up"]]]])))
