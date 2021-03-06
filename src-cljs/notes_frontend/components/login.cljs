(ns notes-frontend.components.login
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notes-frontend.components.buttons :refer [button]]
            [notes-frontend.history :as history]
            [notes-frontend.components.page :as page]
            [notes-frontend.routes :as routes]
            [notes-frontend.validations :as validations]
            [notes-frontend.app-state :as app-state]))

(defn- valid? [{:keys [username password]}]
  (and
    (validations/valid-email? username)
    (validations/valid-password? password)))

(defn login-page [{:keys [className ch login static-url]}]
  (let [{:keys [username password loading]} login
        valid (valid? login)
        submittable (and (not loading) valid)
        focus (if (validations/valid-email? username) :password :username)]
    (om/component
      (html [:div
             {:className (js/classNames "LoginPage" className)
              :style {:backgroundImage (page/page-background-image static-url)}}
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
