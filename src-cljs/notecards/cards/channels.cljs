(ns notecards.cards.channels
  (:require-macros [devcards.core :refer [defcard om-root]]
                   [cljs.core.async.macros :refer [go alt!]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [cljs.core.async :as async :refer [pub sub chan put! <! >! timeout alts! buffer dropping-buffer sliding-buffer]]))

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
#_(console/log "blocking?:" {:sliding (async/unblocking-buffer? (sliding-buffer 3))
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

; This is a pretty good example of pubsub.
; - Pubsub is a "marker" extension of channels.
; - The fundamental channel interface remains unchanged. What changes is how messages are propagated after they enter core.async land.
; Some rules for pub
; - You cannot listen to the raw channel once it is wrapped in a pub (this will multiplex with the pub).
; - pub is a way to SPLIT a channel into multiple channels.
; - sub is a way to LISTEN to a splitted part from the pub channel.
; - ALL subs hear the message that is received by a pub.
; - It is possible to JOIN multiple topic streams onto a single channel using sub. I feel like pub and sub are mostly useful in an imperative manner.
(defn pub-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      (let [ch (chan)
            pub (pub ch #(:topic %))]
        {:ch ch
         :pub pub}))
    om/IWillMount
    (will-mount [_]
      (let [{:keys [pub]} (om/get-state owner)
            red-ch (chan)
            red-backup-ch (chan)
            blue-ch (chan)
            color-ch (chan)
            red-sub (sub pub :red red-ch)
            red-backup-sub (sub pub :red red-backup-ch)
            blue-sub (sub pub :blue blue-ch)
            color-red-sub (sub pub :red color-ch)
            color-blue-sub (sub pub :blue color-ch)]
        (go (loop []
              (let [message (<! red-ch)]
                (console/log "red message:" message)
                (recur))))
        (go (loop []
              (let [message (<! red-backup-ch)]
                (console/log "red backup message:" message)
                (recur))))
        (go (loop []
              (let [message (<! blue-ch)]
                (console/log "blue message:" message)
                (recur))))
        (go (loop []
              (let [message (<! color-ch)]
                (console/log "color message:" message)
                (recur))))))
    om/IRenderState
    (render-state [_ state]
      (let [{:keys [ch]} state]
        (html [:div
               [:div
                [:button.ChannelCards-button.ChannelCards-button--red
                 {:type "button"
                  :on-click (fn [e]
                              (put! ch {:topic :red}))}]]
               [:div
                [:button.ChannelCards-button.ChannelCards-button--blue
                 {:type "button"
                  :on-click (fn [e]
                              (put! ch {:topic :blue}))}]]])))))

#_(defcard pub-card
         (om-root pub-view))