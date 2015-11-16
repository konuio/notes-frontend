(ns notecards.cards.channels
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
      (let [{:keys [c1 c2]} (om/get-state owner)]
        (go (loop []
              (let [[message ch] (alts! [c1 c2])]
                (cond
                  (= ch c1) (console/log "c1:" message)
                  (= ch c2) (console/log "c2:" message))
                (recur))))))
    om/IRenderState
    (render-state [_ state]
      (let [{:keys [c1 c2]} state]
        (html [:div
               [:div
                [:button
                 {:type     "button"
                  :on-click (fn [e]
                              (go
                                (doseq [i (range 10)]
                                  (put! c1 i)
                                  (<! (timeout 500)))))}
                 "Send to c1"]]
               [:div
                [:button
                 {:type     "button"
                  :on-click (fn [e]
                              (go
                                (doseq [i (range 10)]
                                  (put! c2 i)
                                  (<! (timeout 500)))))}
                 "Send to c2"]]])))))

(defcard put-card (om-root put-view))