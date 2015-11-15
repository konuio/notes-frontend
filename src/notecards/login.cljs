(ns notecards.login
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.validations :as validations]
            [notecards.app-state :as app-state]))

(defn- valid? [{:keys [username password]}]
  (and
    (validations/valid-email? username)
    (validations/valid-password? password)))

(defn login-page [{:keys [className ch login]}]
  (let [{:keys [username password]} login
        valid (valid? login)]
    (om/component
      (html [:div
             {:className (js/classNames "LoginPage" className)}
             [:form.LoginPage-card
              {:on-submit (fn [e]
                            (.preventDefault e)
                            (if valid
                              (app-state/post-message! ch {:action :log-in
                                                           :user login})))}
              [:div.LoginPage-field
               [:label.LoginPage-label "Email"]
               [:input.LoginPage-input {:type "text"
                                        :value username
                                        :on-change (fn [e]
                                                     (app-state/post-message! ch {:action :set-login
                                                                                  :login (assoc login :username e.currentTarget.value)}))}]]
              [:div.LoginPage-field
               [:label.LoginPage-label "Password"]
               [:input.LoginPage-input {:type "password"
                                        :value password
                                        :on-change (fn [e]
                                                     (app-state/post-message! ch {:action :set-login
                                                                                  :login (assoc login :password e.currentTarget.value)}))}]]
              [:div.LoginPage-actions
               [:button
                {:type     "submit"
                 :className (js/classNames "LoginPage-login LoginPage-action" (if valid "LoginPage-login--enabled"))}
                "Log in"]
               [:a.LoginPage-signup.LoginPage-action
                {:href     (routes/signup-path)
                 :on-click (fn [e]
                             (.preventDefault e)
                             (.setToken history/history (routes/signup-path)))}
                "Don't have an account? Sign up"]]]]))))
