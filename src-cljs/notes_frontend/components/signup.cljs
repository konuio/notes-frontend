(ns notes-frontend.components.signup
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notes-frontend.app-state :as app-state]
            [notes-frontend.components.buttons :refer [button]]
            [notes-frontend.history :as history]
            [notes-frontend.components.page :as page]
            [notes-frontend.routes :as routes]
            [notes-frontend.validations :as validations]
            [promesa.core :as p]))

(defn- valid? [{:keys [username password confirm]}]
  (and
    (validations/valid-email? username)
    (validations/valid-passwords? password confirm)))

(defn signup-page [{:keys [className ch signup static-url]}]
  (let [{:keys [username password confirm error loading]} signup
        valid (valid? signup)
        submittable (and (not loading) valid)]
    (om/component
      (html [:div
             {:className (js/classNames "SignupPage" className)
              :style {:backgroundImage (page/page-background-image static-url)}}
             [:form.SignupPage-card
              {:on-submit (fn [e]
                            (.preventDefault e)
                            (if submittable
                              (app-state/post-message! ch {:action :sign-up
                                                           :user {:username username
                                                                  :email username
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
              (if error
                [:div.SignupPage-error (get {"duplicate-username" "Username is already registered."
                                             "duplicate-email" "Email is already registered."}
                                            error)])
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

(defn signup-pending-page [{:keys [className static-url]}]
  (om/component
    (html [:div
           {:className (js/classNames "SignupPendingPage" className)
            :style {:backgroundImage (page/page-background-image static-url)}}
           [:div.SignupPendingPage-card
            [:div.SignupPendingPage-title
             "Thanks for signing up!"]
            [:div.SignupPendingPage-subtitle
             "Please check your email for a verification message."]]])))

(defn redeem-signup-page [{:keys [ch className token static-url]}]
  (reify
    om/IWillMount
    (will-mount [_]
      (app-state/post-message! ch {:action :redeem-signup
                                   :token token}))
    om/IRender
    (render [_]
      (html [:div
             {:className (js/classNames "RedeemSignupPage" className)
              :style {:backgroundImage (page/page-background-image static-url)}}]))))
