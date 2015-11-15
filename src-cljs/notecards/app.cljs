(ns notecards.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.app-state :as app-state]
            [notecards.home :as home]
            [notecards.history :as history]
            [notecards.login :as login]
            [notecards.signup :as signup]
            [notecards.routes :as routes]
            [notecards.signup :as signup]
            [cljs.core.async :refer [chan <!]]))

(defn app [{:keys [page] :as data} owner]
  (reify
    om/IInitState
    (init-state [_]
      {:ch (chan)})
    om/IWillMount
    (will-mount [_]
      (let [{:keys [ch]} (om/get-state owner)]
        (go (loop []
              (let [message (<! ch)]
                (console/log "received message:" message)
                (app-state/handle-message! data message)
                (recur))))
        (routes/define-routes! #(app-state/post-message! ch %))
        (history/parse-route-and-listen-for-navigation!)))
    om/IRenderState
    (render-state [_ state]
      (console/log "rendering:" data)
      (case page
        :login (html
                 (om/build login/login-page (merge data state)))
        :signup (html
                  (om/build signup/signup-page (merge data state)))
        :home (html
                (om/build home/home-page (merge data state)))
        nil nil))))
