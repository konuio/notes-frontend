(ns notecards.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.app-state :as app-state]
            [notecards.home :as home]
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
        (routes/define-routes! ch)))
    om/IRenderState
    (render-state [_ state]
      (console/log "rendering:" data)
      (html (om/build (case page
                        :login login/login-page
                        :signup signup/signup-page
                        :home home/home-page)
                      (merge data state))))))
