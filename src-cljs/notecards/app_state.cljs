(ns notecards.app-state
  (:require [om.core :as om]
            [shodan.console :as console]
            [cljs.core.async :refer [put!]]
            [goog.string.format]
            [promesa.core :as p]
            [notecards.api :as api]
            [notecards.app-storage :as app-storage]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.utils :refer [find-index]]))

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
  (om/transact! data #(assoc % :page page)))

(defn set-signup! [data signup]
  (om/transact! data #(assoc % :signup signup)))

(defn sign-up! [data user]
  (om/transact! data #(assoc-in % [:signup :loading] true))
  (-> (api/sign-up user)
      (p/then (fn []
                (om/transact! data #(-> %
                                        (assoc :signup default-signup)
                                        (assoc :login (assoc default-login :username (:username user)))))
                (.setToken history/history (routes/login-path))))))

(defn set-login! [data login]
  (om/transact! data #(assoc % :login login)))

(defn log-in! [data user]
  (om/transact! data #(assoc-in % [:login :loading] true))
  (-> (api/log-in user)
      (p/then (fn [{:keys [token]}]
                (om/transact! data #(-> %
                                        (assoc :login default-login)
                                        (assoc :token token)))
                (app-storage/set-token token)
                (.setToken history/history (routes/home-path))))))

(defn log-out! [data]
  ; TODO Actually send the logout request. Right now we just go to the login page.
  (.setToken history/history (routes/login-path)))

(defn get-notes! [data owner token]
  (let [old-request (om/get-state owner [:requests :get-notes])
        token (or token (:token @data))
        request (-> (api/get-notes token)
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
    (-> (api/update-note note token)
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
    (-> (api/delete-note id token)
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
    (-> (api/create-note {:title ""
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

(defn post-message! [ch message]
  (put! ch message))

(defn handle-message! [data owner {:keys [action] :as message}]
  (case action
    :set-page (set-page! data (:page message))
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
    :hide-tooltip (hide-tooltip! owner)))
