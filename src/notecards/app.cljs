(ns notecards.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.app-state :as app-state]
            [notecards.history :as history]
            [notecards.login :as login]
            [notecards.routes :as routes]
            [notecards.signup :as signup]
            [cljs.core.async :refer [chan <!]]))

(defn home-page [data]
  (om/component
    (html [:div
           [:h1 "Home..."]
           [:div
            [:a {:href "#"
                 :on-click (fn [e]
                             (.preventDefault e)
                             (.setToken history/history (routes/login-path))
                             nil)}
             "Log in"]]])))

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
                        :home home-page)
                      (merge data state))))))
