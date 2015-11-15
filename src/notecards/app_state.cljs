(ns notecards.app-state
  (:require [om.core :as om]
            [shodan.console :as console]))

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
