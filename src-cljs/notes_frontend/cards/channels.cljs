(ns notes-frontend.cards.channels
  (:require-macros [devcards.core :refer [defcard om-root]]
                   [cljs.core.async.macros :refer [go alt!]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [cljs.core.async :as async :refer [to-chan tap mult onto-chan offer! pipe poll! pub sub chan put! <! >! timeout alts! buffer dropping-buffer sliding-buffer]]
            [goog.string.format]))

(defn put-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:c1 (chan)
       :c2 (chan)})
    om/IWillMount
    (will-mount [_]
      (let [{:keys [c1 c2]} (om/get-state owner)
            mix-ch (chan)
            mix (async/mix mix-ch)]
        (async/admix mix c1)
        (async/admix mix c2)
        (go (loop []
              (let [message (<! mix-ch)]
                (console/log "received:" message)
                (recur))))))
    om/IRenderState
    (render-state [_ state]
      (let [{:keys [c1 c2]} state]
        (html [:div
               ; TODO mute/pause selector for c1
               [:div
                [:button
                 {:type     "button"
                  :on-click (fn [e]
                              (go
                                (doseq [i (range 10)]
                                  (put! c1 i)
                                  (<! (timeout 100)))))}
                 "Send to c1"]]
               [:div
                [:button
                 {:type     "button"
                  :on-click (fn [e]
                              (go
                                (doseq [i (range 10)]
                                  (put! c2 (+ 10 i))
                                  (<! (timeout 100)))))}
                 "Send to c2"]]])))))

(defcard put-card (om-root put-view))
