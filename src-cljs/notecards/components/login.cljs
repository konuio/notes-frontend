(ns notecards.components.login
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.components.buttons :refer [button]]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.validations :as validations]
            [notecards.app-state :as app-state]))

(defn- valid? [{:keys [username password]}]
  (and
    (validations/valid-email? username)
    (validations/valid-password? password)))

(defn login-page [{:keys [className ch login]}]
  (let [{:keys [username password loading]} login
        valid (valid? login)
        submittable (and (not loading) valid)
        focus (if (validations/valid-email? username) :password :username)]
    (om/component
      (html [:div
             {:className (js/classNames "LoginPage" className)}
             [:form.LoginPage-card
              {:on-submit (fn [e]
                            (.preventDefault e)
                            (if submittable
                              (app-state/post-message! ch {:action :log-in
                                                           :user login})))}
              [:div.LoginPage-field
               [:label.LoginPage-label "Email"]
               [:input.LoginPage-input {:type "text"
                                        :value username
                                        :autoFocus (= focus :username)
                                        :on-change (fn [e]
                                                     (app-state/post-message! ch {:action :set-login
                                                                                  :login (assoc login :username e.currentTarget.value)}))}]]
              [:div.LoginPage-field
               [:label.LoginPage-label "Password"]
               [:input.LoginPage-input {:type "password"
                                        :value password
                                        :autoFocus (= focus :password)
                                        :on-change (fn [e]
                                                     (app-state/post-message! ch {:action :set-login
                                                                                  :login (assoc login :password e.currentTarget.value)}))}]]
              [:div.LoginPage-actions
               (om/build button
                         {:type "submit"
                          :className "LoginPage-login"
                          :enabledClassName "LoginPage-login--enabled"
                          :enabled submittable
                          :content "Log in"})
               (om/build button
                         {:className "LoginPage-signup"
                          :content "Don't have an account? Sign up"
                          :on-click (fn [e]
                                      (.preventDefault e)
                                      (.setToken history/history (routes/signup-path)))})]]]))))
