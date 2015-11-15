(ns notecards.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [secretary.core :as secretary]
            [notecards.app-state :as app-state]
            [notecards.history :as history]
            [cljs.core.async :refer [chan <!]]
            [notecards.app-channel :as app-channel]))

(defn login-page [data]
  (om/component
    (html [:div "Login..."
           [:div
            [:a {:href "#"
                 :on-click (fn [e]
                             (.preventDefault e)
                             (.setToken history/history (history/signup-path))
                             nil)}
             "Sign up"]]
           [:div
            [:a {:href "#"
                 :on-click (fn [e]
                             (.preventDefault e)
                             (.setToken history/history (history/home-path))
                             nil)}
             "Home"]]])))

(defn signup-page [data]
  (om/component
    (html [:div "Signup..."
           [:a {:href "#"
                :on-click (fn [e]
                            (.preventDefault e)
                            (.setToken history/history (history/login-path))
                            nil)}
            "Log in"]])))

(defn home-page [data]
  (om/component
    (html [:div
           [:h1 "Home..."]
           [:div
            [:a {:href "#"
                 :on-click (fn [e]
                             (.preventDefault e)
                             (.setToken history/history (history/login-path))
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
                (app-channel/handle-message! data message)
                (recur))))
        (history/define-routes! ch)))
    om/IRenderState
    (render-state [_ _]
      (console/log "rendering:" data)
      (html (om/build (case page
                        :login login-page
                        :signup signup-page
                        :home home-page)
                      data)))))
