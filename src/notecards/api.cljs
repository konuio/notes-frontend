(ns notecards.api
  (:require [shodan.console :as console]
            [goog.string.format]
            [promesa.core :as p]))

(defn sign-up [user]
  (do
    (console/log "signing up:" user)
    (-> (p/promise
          (.ajax js/$ (clj->js {:type "POST"
                                :url "http://localhost:8080/user"
                                :contentType "application/json"
                                :data (-> user
                                          clj->js
                                          js/JSON.stringify)})))
        (p/then (fn [response]
                  (console/log "signed up")
                  response)))))

(defn log-in [user]
  (do
    (console/log "logging in:" user)
    (-> (p/promise
          (.ajax js/$ (clj->js {:type "POST"
                                :url "http://localhost:8080/login"
                                :contentType "application/json"
                                :data (-> user
                                          clj->js
                                          js/JSON.stringify)})))
        (p/then (fn [response]
                  (console/log "logged in")
                  response)))))

(defn get-notes []
  (do
    (console/log "getting notes")
    (-> (p/promise
          (.ajax js/$ (clj->js {:url "http://localhost:8080/authenticated/note"})))
        (p/then (fn [response]
                  (let [notes (into []
                                    (map #(clojure.set/rename-keys % {:_id :id}))
                                    (js->clj response :keywordize-keys true))]
                    (console/log "got notes:" notes)
                    notes))))))

(defn create-note [note]
  (do
    (console/log "creating note:" note)
    (-> (p/promise
          (.ajax js/$ (clj->js {:type "POST"
                                :url "http://localhost:8080/authenticated/note"
                                :contentType "application/json"
                                :data (-> note
                                          clj->js
                                          js/JSON.stringify)})))
        (p/then (fn [response]
                  (let [note (-> response
                                 js->clj
                                 (clojure.set/rename-keys {:_id :id}))]
                    (console/log "created note:" note)
                    note))))))

(defn delete-note [id]
  (do
    (console/log "deleting note:" id)
    (-> (p/promise
          (.ajax js/$ (clj->js {:type "DELETE"
                                :url (goog.string.format "http://localhost:8080/authenticated/note/%s" id)})))
        (p/then (fn [response]
                  (console/log "deleted note")
                  response)))))

(defn update-note [note]
  (do
    (console/log "updating note:" note)
    (-> (p/promise
          (.ajax js/$ (clj->js {:type "PUT"
                                :url (goog.string.format "http://localhost:8080/authenticated/note/%s" (:id note))
                                :contentType "application/json"
                                :data (-> note
                                          (dissoc :id)
                                          clj->js
                                          js/JSON.stringify)})))
        (p/then (fn [response]
                  (console/log "updated note")
                  response)))))
