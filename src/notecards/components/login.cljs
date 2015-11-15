(ns notecards.components.login
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [shodan.console :as console :include-macros true]
            [goog.string.format]
            [clojure.string]
            [notecards.components.button :refer [button]]))

(defn valid-username [email]
  (re-find #".@." email))

(defn valid-password [password]
  (seq password))

(defn valid-passwords [password confirm]
  (and
    (valid-password password)
    (valid-password confirm)
    (= password confirm)))

(defn field [{:keys [type value label className]}]
  (om/component
    (html [:div
           {:className (js/classNames "field" className)}
           [:label.field-label label]
           [:input.field-input {:type type
                                :defaultValue value}]])))

(defn login [{:keys [username password className]}]
  (om/component
    (html [:div
           {:className (js/classNames "login" className)}
           [:div.login-fields
            (om/build
              field
              {:type "text"
               :className "login-field"
               :value username
               :label "Email"})
            (om/build
              field
              {:type "password"
               :className "login-field"
               :value password
               :label "Password"})]
           [:div.login-actions
            (om/build
              button
              {:className "login-actionButton"
               :title "Log In"
               :enabled (and
                          (valid-username username)
                          (valid-password password))})
            (om/build
              button
              {:className "login-actionButton"
               :title "Sign Up"
               :type "transparent"})]])))

#_(defcard
  devcard-login*
  (om-root login)
  {:className "devcards-login"
   :username ""
   :password ""})

(defn signup [{:keys [username password confirm className]}]
  (om/component
    (html [:div
           {:className (js/classNames "login" "login--signup" className)}
           [:div.login-fields
            (om/build
              field
              {:type "text"
               :className "login-field"
               :value username
               :label "Email"})
            (om/build
              field
              {:type "password"
               :className "login-field"
               :value password
               :label "Password"})
            (om/build
              field
              {:type "password"
               :className "login-field"
               :value confirm
               :label "Confirm Password"})]
           [:div.login-actions
            (om/build
              button
              {:className "login-actionButton"
               :title "Sign Up"
               :enabled (and
                          (valid-username username)
                          (valid-passwords password confirm))})
            (om/build
              button
              {:className "login-actionButton"
               :title "Log In"
               :type "transparent"})]])))

#_(defcard
  devcard-signup
  (om-root signup)
  {:className "devcards-signup"
   :username ""
   :password ""
   :confirm ""})