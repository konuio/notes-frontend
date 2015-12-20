(ns notes-frontend.api
  (:require [shodan.console :as console]
            [goog.string.format]
            [promesa.core :as p]))

(defn authorization-header [token]
  {"Authorization" (goog.string.format "Token %s" token)})

(defn sign-up [url user]
  (console/log "signing up:" user)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "POST"
                              :url (goog.string.format "%s/signup" url)
                              :contentType "application/json"
                              :data (-> user
                                        clj->js
                                        js/JSON.stringify)})))
      (p/then (fn [response]
                (console/log "signed up")
                (js->clj response :keywordize-keys true)))))

(defn log-in [url user]
  (console/log "logging in:" user)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "POST"
                              :url (goog.string.format "%s/login" url)
                              :contentType "application/json"
                              :data (-> user
                                        clj->js
                                        js/JSON.stringify)})))
      (p/then (fn [response]
                (console/log "logged in")
                (js->clj response :keywordize-keys true)))))

(defn get-notes [url token]
  (console/log "getting notes")
  (-> (p/promise
        (.ajax js/$ (clj->js {:url (goog.string.format "%s/note" url)
                              :headers (authorization-header token)})))
      .cancellable
      (p/then (fn [response]
                (let [notes (into []
                                  (map #(clojure.set/rename-keys % {:_id :id}))
                                  (js->clj response :keywordize-keys true))]
                  (console/log "got notes:" notes)
                  notes)))))

(defn create-note [url note token]
  (console/log "creating note:" note)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "POST"
                              :url (goog.string.format "%s/note" url)
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

(defn delete-note [url id token]
  (console/log "deleting note:" id)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "DELETE"
                              :url (goog.string.format "%s/note/%s" url id)
                              :headers (authorization-header token)})))
      (p/then (fn [response]
                (console/log "deleted note")
                response))))

(defn update-note [url note token]
  (console/log "updating note:" note)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "PUT"
                              :url (goog.string.format "%s/note/%s" url (:id note))
                              :contentType "application/json"
                              :headers (authorization-header token)
                              :data (-> note
                                        (dissoc :id)
                                        clj->js
                                        js/JSON.stringify)})))
      (p/then (fn [response]
                (console/log "updated note")
                response))))

(defn redeem-signup [url token]
  (console/log "redeeming signup:" token)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "POST"
                              :url (goog.string.format "%s/redeem-signup" url)
                              :contentType "application/json"
                              :headers (authorization-header token)
                              :data (-> {:token token}
                                        clj->js
                                        js/JSON.stringify)})))
      (p/then (fn [response]
                (console/log "redeemed signup")
                (js->clj response :keywordize-keys true)))))
