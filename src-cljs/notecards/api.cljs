(ns notecards.api
  (:require [shodan.console :as console]
            [goog.string.format]
            [promesa.core :as p]))

(defn authorization-header [token]
  {"Authorization" (goog.string.format "Token %s" token)})

(defn sign-up [user]
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
                response))))

(defn log-in [user]
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
                (js->clj response :keywordize-keys true)))))

(defn get-notes [token]
  (console/log "getting notes")
  (-> (p/promise
        (.ajax js/$ (clj->js {:url "http://localhost:8080/note"
                              :headers (authorization-header token)})))
      .cancellable
      (p/then (fn [response]
                (let [notes (into []
                                  (map #(clojure.set/rename-keys % {:_id :id}))
                                  (js->clj response :keywordize-keys true))]
                  (console/log "got notes:" notes)
                  notes)))))

(defn create-note [note token]
  (console/log "creating note:" note)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "POST"
                              :url "http://localhost:8080/note"
                              :contentType "application/json"
                              :headers (authorization-header token)
                              :data (-> note
                                        clj->js
                                        js/JSON.stringify)})))
      (p/then (fn [response]
                (let [note (-> response
                               (js->clj :keywordize-keys true)
                               (clojure.set/rename-keys {:_id :id}))]
                  (console/log "created note:" note)
                  note)))))

(defn delete-note [id token]
  (console/log "deleting note:" id)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "DELETE"
                              :url (goog.string.format "http://localhost:8080/note/%s" id)
                              :headers (authorization-header token)})))
      (p/then (fn [response]
                (console/log "deleted note")
                response))))

(defn update-note [note token]
  (console/log "updating note:" note)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "PUT"
                              :url (goog.string.format "http://localhost:8080/note/%s" (:id note))
                              :contentType "application/json"
                              :headers (authorization-header token)
                              :data (-> note
                                        (dissoc :id)
                                        clj->js
                                        js/JSON.stringify)})))
      (p/then (fn [response]
                (console/log "updated note")
                response))))
