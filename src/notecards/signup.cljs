(ns notecards.signup
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.app-state :as app-state]))

(defn signup-page [{:keys [className ch]}]
  (om/component
    (html [:div
           {:className (js/classNames "SignupPage" className)}
           [:form.SignupPage-card
            [:div.SignupPage-field
             [:label.SignupPage-label "Email"]
             [:input.SignupPage-input {:type "text"
                                      :defaultValue "mking@example.com"}]]
            [:div.SignupPage-field
             [:label.SignupPage-label "Password"]
             [:input.SignupPage-input {:type "password"
                                      :defaultValue "password"}]]
            [:div.SignupPage-field
             [:label.SignupPage-label "Confirm Password"]
             [:input.SignupPage-input {:type "password"
                                       :defaultValue "password"}]]
            [:div.SignupPage-actions
             [:button.SignupPage-login.SignupPage-action
              {:type     "submit"
               :on-click (fn [e]
                           (app-state/post-message! ch {:action :sign-up
                                                        :username "foo"
                                                        :password "foo"}))}
              "Sign up"]
             [:a.SignupPage-signup.SignupPage-action
              {:href     (routes/login-path)
               :on-click (fn [e]
                           (.preventDefault e)
                           (.setToken history/history (routes/login-path)))}
              "Log in"]]]])))
