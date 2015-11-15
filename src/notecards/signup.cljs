(ns notecards.signup
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.validations :as validations]
            [notecards.app-state :as app-state]
            [promesa.core :as p]))

(defn- valid? [{:keys [username password confirm]}]
  (and
    (validations/valid-email? username)
    (validations/valid-passwords? password confirm)))

(defn signup-page [{:keys [className ch signup]}]
  (let [{:keys [username password confirm]} signup
        valid (valid? signup)]
    (om/component
      (html [:div
             {:className (js/classNames "SignupPage" className)}
             [:form.SignupPage-card
              {:on-submit (fn [e]
                            (.preventDefault e)
                            (if valid
                              (app-state/post-message! ch {:action :sign-up
                                                           :user {:username username
                                                                  :password password}})))}
              [:div.SignupPage-field
               [:label.SignupPage-label "Email"]
               [:input.SignupPage-input {:type "text"
                                         :value username
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
               [:button
                {:type      "submit"
                 :className (js/classNames "SignupPage-signup SignupPage-action" (if valid "SignupPage-signup--enabled"))}
                "Sign up"]
               [:a.SignupPage-login.SignupPage-action
                {:href     (routes/login-path)
                 :on-click (fn [e]
                             (.preventDefault e)
                             (.setToken history/history (routes/login-path)))}
                "Have an account? Log in"]]]]))))
