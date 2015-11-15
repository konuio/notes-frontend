(ns notecards.api
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [shodan.console :as console :include-macros true]
            [goog.string.format]
            [clojure.string]
            [notecards.components.button :refer [button]]
            [org.bluebird]
            [promesa.core :as p]
            [cats.core :as m]))

(defn sign-up [user]
  (p/promise
    (.ajax js/$ (clj->js {:type "POST"
                          :url "http://localhost:8080/user"
                          :contentType "application/json"
                          :data (-> user
                                    clj->js
                                    js/JSON.stringify)}))))

(defn log-in [user]
  (p/promise
    (.ajax js/$ (clj->js {:type "POST"
                          :url "http://localhost:8080/login"
                          :contentType "application/json"
                          :data (-> user
                                    clj->js
                                    js/JSON.stringify)}))))

(defn get-notes []
  (-> (p/promise
        (.ajax js/$ (clj->js {:url "http://localhost:8080/authenticated/note"})))
      (p/then (fn [response]
                (js->clj response)))))

(defn create-note [note]
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "POST"
                              :url "http://localhost:8080/authenticated/note"
                              :contentType "application/json"
                              :data (-> note
                                        clj->js
                                        js/JSON.stringify)})))
      (p/then (fn [response]
                (-> response
                      js->clj
                      (clojure.set/rename-keys {:_id :id}))))))

(defn delete-note [id]
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "DELETE"
                              :url (goog.string.format "http://localhost:8080/authenticated/note/%s" id)})))))

(defn update-note [note]
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "PUT"
                              :url (goog.string.format "http://localhost:8080/authenticated/note/%s" (:id note))
                              :contentType "application/json"
                              :data (-> note
                                        (dissoc :id)
                                        clj->js
                                        js/JSON.stringify)})))))

#_(defn print-result [promise]
  (p/branch promise
            (fn [response]
              (console/log "Response:" response))
            (fn [error]
              (console/log "Error:" error))))

#_(defn api-tester [{:keys [className]}]
  (om/component
    (html [:div
           {:className (js/classNames "apiTester" className)}
           (om/build
             button
             {:className "apiTester-button"
              :title "Sign up"
              :on-click (fn []
                          (-> (sign-up {:username "mking+1"
                                        :password "password"})
                              print-result))})
           (om/build
             button
             {:className "apiTester-button"
              :title "Log in"
              :on-click (fn []
                          (-> (log-in {:username "mking+1"
                                       :password "password"})
                              print-result))})
           (om/build
             button
             {:className "apiTester-button"
              :title "Get notes"
              :on-click (fn []
                          (-> (get-notes)
                              print-result))})
           (om/build
             button
             {:className "apiTester-button"
              :title "Create note"
              :on-click (fn []
                          (-> (create-note {:title "Title"
                                            :data "Data"
                                            :notebook nil})
                              print-result))})
           (om/build
             button
             {:className "apiTester-button"
              :title "Delete note"
              :on-click (fn []
                          (-> (delete-note "5647cfc5bee8575380a1a104")
                              print-result))})
           (om/build
             button
             {:className "apiTester-button"
              :title "Update note"
              :on-click (fn []
                          (-> (update-note (let [revision 2]
                                             {:id "5647cb73bee8575380a1a100"
                                              :title (goog.string.format "Title %d" revision)
                                              :data (goog.string.format "Data %d" revision)}))
                              print-result))})])))

#_(defcard
  devcard-api-tester
  (om-root api-tester)
  {:className "devcards-apiTester"})