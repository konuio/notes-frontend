(ns notes-frontend.app-state
  (:require [om.core :as om]
            [shodan.console :as console]
            [cljs.core.async :refer [put!]]
            [goog.string.format]
            [promesa.core :as p]
            [notes-frontend.api :as api]
            [notes-frontend.app-storage :as app-storage]
            [notes-frontend.history :as history]
            [notes-frontend.routes :as routes]
            [notes-frontend.utils :refer [find-index]]))

(def default-signup {:username ""
                     :password ""
                     :confirm ""
                     :loading false})

(def default-login {:username ""
                    :password ""
                    :loading false})

(def default-pending-note {})

(defonce app-state (atom {:page nil
                          :signup default-signup
                          :login default-login
                          :token nil
                          :notes []
                          :home {:selected nil
                                 :pending default-pending-note
                                 :loading false}}))

(defn set-page! [data page]
  (om/transact! data #(merge % page)))

(defn set-signup! [data signup]
  (om/transact! data #(assoc % :signup signup)))

(defn sign-up! [data user]
  (om/transact! data #(update % :signup (fn [signup] (merge signup {:loading true
                                                                    :error nil}))))
  (-> (api/sign-up (:url @data) user)
      (p/then (fn [{:keys [error]}]
                (if (and error (not= error "SUCCESS"))
                  (om/transact! data #(update % :signup (fn [signup] (merge signup {:loading false
                                                                                    :error error}))))
                  (do (om/transact! data #(-> %
                                              (assoc :signup default-signup)
                                              (assoc :login (assoc default-login :username (:username user)))))
                      (.setToken history/history (routes/signup-pending-path))))))))

(defn set-login! [data login]
  (om/transact! data #(assoc % :login login)))

(defn log-in-with-token! [data token]
  (om/transact! data #(-> %
                          (assoc :login default-login)
                          (assoc :token token)))
  (app-storage/set-token token)
  (.setToken history/history (routes/home-path)))

(defn log-in! [data user]
  (om/transact! data #(assoc-in % [:login :loading] true))
  (-> (api/log-in (:url @data) user)
      (p/then (fn [{:keys [token]}]
                (log-in-with-token! data token)))))

(defn log-out! [data]
  ; TODO Actually send the logout request. Right now we just go to the login page.
  (.setToken history/history (routes/login-path)))

(defn get-notes! [data owner token]
  (let [old-request (om/get-state owner [:requests :get-notes])
        token (or token (:token @data))
        url (:url @data)
        request (-> (api/get-notes url token)
                    (p/then (fn [notes]
                              (om/transact! data (fn [data]
                                                   (-> data
                                                       (assoc :notes notes)
                                                       (assoc-in [:home :selected] (:id (first notes)))
                                                       (assoc :token token)))))))]
    (.cancel old-request)
    (om/set-state! owner [:requests :get-notes] request)))

(defn select-note! [data id]
  (om/transact! data #(-> %
                          (assoc-in [:home :selected] id)
                          (assoc-in [:home :pending] default-pending-note))))

(defn set-pending-note! [data note]
  (om/transact! data #(assoc-in % [:home :pending] note)))

(defn save-note! [data note]
  (let [{:keys [token]} @data]
    (om/transact! data #(assoc-in % [:home :loading] true))
    (-> (api/update-note (:url @data) note token)
        (p/then (fn []
                  (om/transact! data (fn [data]
                                       (let [note-index (find-index #(= (:id note) (:id %)) (:notes data))]
                                         (-> data
                                             (assoc-in [:notes note-index] note)
                                             (assoc-in [:home :pending] default-pending-note)
                                             (assoc-in [:home :loading] false))))))))))

(defn cancel-note! [data]
  (om/transact! data #(assoc-in % [:home :pending] default-pending-note)))

(defn delete-note! [data id]
  (console/log "deleting with data:" data)
  (let [{:keys [token]} @data]
    (om/transact! data #(assoc-in % [:home :loading] true))
    (-> (api/delete-note (:url @data) id token)
        (p/then (fn []
                  (om/transact! data (fn [data]
                                       (let [index (find-index #(= id (:id %)) (:notes data))]
                                         (let [old-notes (:notes data)
                                               notes (vec (concat (subvec old-notes 0 index) (subvec old-notes (inc index))))]
                                           (-> data
                                               (assoc :notes notes)
                                               (assoc-in [:home :selected] (:id (first notes)))
                                               (assoc-in [:home :loading] false)))))))))))

(defn create-note! [data]
  (let [{:keys [token]} @data]
    (om/transact! data #(assoc-in % [:home :loading] true))
    (-> (api/create-note (:url @data)
                         {:title ""
                          :data ""}
                         token)
        (p/then (fn [note]
                  (om/transact! data (fn [data]
                                       (-> data
                                           (update :notes #(conj % note))
                                           (assoc-in [:home :selected] (:id note))
                                           (assoc-in [:home :loading] false)))))))))

(defn show-tooltip! [owner tooltip]
  (om/set-state! owner :tooltip tooltip))

(defn hide-tooltip! [owner]
  (om/set-state! owner :tooltip nil))

(defn redeem-signup! [data token]
  (-> (api/redeem-signup (:url @data) token)
      (p/branch (fn [{:keys [token]}]
                  (log-in-with-token! data token))
              (fn []
                (.setToken history/history (routes/login-path))))))

(defn post-message! [ch message]
  (put! ch message))

(defn handle-message! [data owner {:keys [action] :as message}]
  (case action
    :set-page (set-page! data message)
    :set-signup (set-signup! data (:signup message))
    :sign-up (sign-up! data (:user message))
    :set-login (set-login! data (:login message))
    :log-in (log-in! data (:user message))
    :log-out (log-out! data)
    :get-notes (get-notes! data owner (:token message))
    :select-note (select-note! data (:id message))
    :set-pending-note (set-pending-note! data (:note message))
    :save-note (save-note! data (:note message))
    :cancel-note (cancel-note! data)
    :delete-note (delete-note! data (:id message))
    :create-note (create-note! data)
    :show-tooltip (show-tooltip! owner (:tooltip message))
    :hide-tooltip (hide-tooltip! owner)
    :redeem-signup (redeem-signup! data (:token message))))
