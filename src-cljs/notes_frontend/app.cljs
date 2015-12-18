(ns notes-frontend.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notes-frontend.app-state :as app-state]
            [notes-frontend.components.home :as home]
            [notes-frontend.components.login :as login]
            [notes-frontend.components.signup :as signup]
            [notes-frontend.history :as history]
            [notes-frontend.routes :as routes]
            [cljs.core.async :refer [chan <!]]
            [promesa.core :as p]))

(defn app [{:keys [page] :as data} owner]
  (reify
    om/IInitState
    (init-state [_]
      {:ch       (chan)
       :requests {:get-notes (p/promise nil)}
       :tooltip nil})
    om/IWillMount
    (will-mount [_]
      (let [{:keys [ch]} (om/get-state owner)]
        (go (loop []
              (let [message (<! ch)]
                (console/log "received message:" (:action message))
                (app-state/handle-message! data owner message)
                (recur))))
        (routes/define-routes! #(app-state/post-message! ch %))
        (history/parse-route-and-listen-for-navigation!)))
    om/IRenderState
    (render-state [_ state]
      (console/log "rendering:" data)
      (html [:div
             (case page
               :login (html
                        (om/build login/login-page (merge data state)))
               :signup (html
                         (om/build signup/signup-page (merge data state)))
               :home (html
                       (om/build home/home-page (merge data state)))
               nil nil)]))))
