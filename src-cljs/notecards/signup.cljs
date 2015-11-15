(ns notecards.signup
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.app-state :as app-state]
            [notecards.buttons :refer [button]]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.validations :as validations]
            [promesa.core :as p]))

(defn- valid? [{:keys [username password confirm]}]
  (and
    (validations/valid-email? username)
    (validations/valid-passwords? password confirm)))

(defn signup-page [{:keys [className ch signup]}]
  (let [{:keys [username password confirm loading]} signup
        valid (valid? signup)
        submittable (and (not loading) valid)]
    (om/component
      (html [:div
             {:className (js/classNames "SignupPage" className)}
             [:form.SignupPage-card
              {:on-submit (fn [e]
                            (.preventDefault e)
                            (if submittable
                              (app-state/post-message! ch {:action :sign-up
                                                           :user {:username username
                                                                  :password password}})))}
              [:div.SignupPage-field
               [:label.SignupPage-label "Email"]
               [:input.SignupPage-input {:type "text"
                                         :value username
                                         :autoFocus true
                                         :on-change (fn [e]
                                                      (app-state/post-message! ch {:action :set-signup
                                                                                   :signup (assoc signup :username e.currentTarget.value)}))}]]
              [:div.SignupPage-field
               [:label.SignupPage-label "Password"]
               [:input.SignupPage-input {:type "password"
                                         :value password
                                         :on-change (fn [e]
                                                      (app-state/post-message! ch {:action :set-signup
                                                                                   :signup (assoc signup :password e.currentTarget.value)}))}]]
              [:div.SignupPage-field
               [:label.SignupPage-label "Confirm Password"]
               [:input.SignupPage-input {:type "password"
                                         :value confirm
                                         :on-change (fn [e]
                                                      (app-state/post-message! ch {:action :set-signup
                                                                                   :signup (assoc signup :confirm e.currentTarget.value)}))}]]
              [:div.SignupPage-actions
               (om/build button
                         {:type "submit"
                          :className "SignupPage-signup"
                          :enabledClassName "SignupPage-signup--enabled"
                          :enabled submittable
                          :content "Sign up"})
               (om/build button
                         {:className "SignupPage-login"
                          :content "Have an account? Log in"
                          :on-click (fn [e]
                                      (.preventDefault e)
                                      (.setToken history/history (routes/login-path)))})]]]))))
