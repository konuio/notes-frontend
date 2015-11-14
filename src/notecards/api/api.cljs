(ns notecards.api.api
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [shodan.console :as console :include-macros true]
            [goog.string.format]
            [clojure.string]
            [notecards.components.button :refer [button]]
            [org.bluebird]
            [promesa.core :as p]
            [cats.core :as m]
            [clojure.set]))

(defn sign-up [email password]
  (p/promise
    (.ajax js/$ (clj->js {:type "POST"
                          :url "http://localhost:8080/user"
                          :contentType "application/json"
                          :data (.stringify js/JSON (clj->js {:username email
                                                              :password password}))}))))

(defn log-in [email password]
  (p/promise
    (.ajax js/$ (clj->js {:type "POST"
                          :url "http://localhost:8080/login"
                          :contentType "application/json"
                          :data (.stringify js/JSON (clj->js {:username email
                                                              :password password}))}))))

(defn api-tester [{:keys [className]}]
  (om/component
    (html [:div
           {:className (js/classNames "apiTester" className)}
           (om/build
             button
             {:className "apiTester-button"
              :title "Sign up"
              :on-click (fn []
                          (-> (sign-up "mking+1" "password")
                              (p/then (fn [response]
                                        (console/log "Response:" response)
                                        response))))})
           (om/build
             button
             {:className "apiTester-button"
              :title "Log in"
              :on-click (fn []
                          (-> (log-in "mking" "password")
                              (p/then (fn [response]
                                        (console/log "Response:" response)
                                        response))))})])))

(defcard
  devcard-api-tester
  (om-root api-tester)
  {:className "devcards-apiTester"})