(ns notecards.app-state
  (:require [om.core :as om]
            [shodan.console :as console]
            [cljs.core.async :refer [put!]]
            [goog.string.format]))

(defonce app-state (atom {:page :home
                          :notes (into []
                                       (map (fn [i]
                                              {:id (str i)
                                               :title (goog.string.format "Title %d" i)
                                               :data (goog.string.format "Data %d" i)
                                               :notebook nil}))
                                       (range 20))}))

(defn set-page! [data page]
  (console/log "setting page:" page)
  (om/transact! data #(assoc % :page page)))

(defn post-message! [ch message]
  (put! ch message))

(defn handle-message! [data {:keys [action] :as message}]
  (case action
    :set-page (set-page! data (:page message))))
