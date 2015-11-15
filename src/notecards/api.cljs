(ns notecards.api
  (:require [shodan.console :as console]
            [goog.string.format]
            [promesa.core :as p]))

(defn sign-up [user]
  (do
    (console/log "signing up:" user)
    (p/promise
      (.ajax js/$ (clj->js {:type "POST"
                            :url "http://localhost:8080/user"
                            :contentType "application/json"
                            :data (-> user
                                      clj->js
                                      js/JSON.stringify)})))))

(defn log-in [user]
  #_(console/log "logging in:" user)
  (p/promise
    (.ajax js/$ (clj->js {:type "POST"
                          :url "http://localhost:8080/login"
                          :contentType "application/json"
                          :data (-> user
                                    clj->js
                                    js/JSON.stringify)}))))

(defn get-notes []
  #_(console/log "getting notes")
  (-> (p/promise
        (.ajax js/$ (clj->js {:url "http://localhost:8080/authenticated/note"})))
      (p/then (fn [response]
                (js->clj response)))))

(defn create-note [note]
  #_(console/log "creating note:" note)
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
  #_(console/log "deleting note:" id)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "DELETE"
                              :url (goog.string.format "http://localhost:8080/authenticated/note/%s" id)})))))

(defn update-note [note]
  #_(console/log "updating note:" note)
  (-> (p/promise
        (.ajax js/$ (clj->js {:type "PUT"
                              :url (goog.string.format "http://localhost:8080/authenticated/note/%s" (:id note))
                              :contentType "application/json"
                              :data (-> note
                                        (dissoc :id)
                                        clj->js
                                        js/JSON.stringify)})))))
