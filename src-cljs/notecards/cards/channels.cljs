(ns notecards.cards.channels
  (:require-macros [devcards.core :refer [defcard om-root]]
                   [cljs.core.async.macros :refer [go alt!]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [cljs.core.async :as async :refer [chan put! <! >! timeout alts! buffer dropping-buffer sliding-buffer]]))

; Unused from cljs
; <!! (blocking take)
; >!! (blocking put)
; alt!! (blocking alt)
; alts!! (blocking alts)

; Questions
; mix?
; admix?

; Buffer kinds
; - For nonblocking buffers, we can always immediately put a message into the queue (the message might evict existing messages).
; - For blocking buffers, we are stuck if the queue is full. I feel like, if we use put! onto a blocking buffer, we end up with an infinite queue (the queued puts).
(console/log "blocking?:" {:sliding (async/unblocking-buffer? (sliding-buffer 3))
                           :dropping (async/unblocking-buffer? (dropping-buffer 3))
                           :buffer (async/unblocking-buffer? (buffer 3))})

; This is a really good example for how channels work at a basic level.
(defn put-view [data owner]
    (reify
      om/IInitState
      (init-state [_]
        {:ch (chan)})
      om/IWillMount
      (will-mount [_]
        (let [{:keys [ch]} (om/get-state owner)]
          (go (loop []
                (let [message (<! ch)]
                  (console/log "received:" message)
                  (recur))))))
      om/IRenderState
      (render-state [_ state]
        (let [{:keys [ch]} state]
          (html [:div
                 [:div
                  [:button
                   {:type "button"
                    :on-click (fn [e]
                                (put! ch "hello"))}
                   "Put"]]])))))

#_(defcard put-card
           (om-root put-view))

; This is a really good example of how buffers work. Change the buffer to different types to see how they handle overflow.
(defn buffer-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:ch (chan (sliding-buffer 3))
       :counter 0})
    om/IWillMount
    (will-mount [_]
      (let [{:keys [ch]} (om/get-state owner)]
        (go (loop []
              (let [message (<! ch)]
                (console/log "received:" message)
                (recur))))))
    om/IRenderState
    (render-state [_ state]
      (let [{:keys [ch counter]} state]
        (html [:div
               [:div
                [:button
                 {:type "button"
                  :on-click (fn [e]
                              (let [max 10]
                                (doseq [i (range max)]
                                  (put! ch (+ i counter)))
                                (om/set-state! owner :counter (+ counter max))))}
                 "Enqueue"]]])))))

#_(defcard buffer-card
         (om-root buffer-view))
