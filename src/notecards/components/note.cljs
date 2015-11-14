(ns notecards.note
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [shodan.console :as console :include-macros true]
            [goog.string.format]
            [clojure.string]
            [notecards.button :refer [button]]))

(defn note-detail [{:keys [title body changed className]}]
  (om/component
    (html [:div
           {:className (js/classNames "noteDetail" className)}
           [:input.noteDetail-title {:type "text"
                                     :defaultValue title}]
           [:textarea.noteDetail-body {:defaultValue body}]
           [:div.noteDetail-actions
            [:div.noteDetail-actionGroup
             (om/build
               button
               {:className "noteDetail-actionButton"
                :iconClassName "ion-trash-b"})]
            [:div.noteDetail-actionGroup
             (om/build
               button
               {:className "noteDetail-actionButton"
                :iconClassName "ion-close"
                :enabled changed})
             (om/build
               button
               {:className "noteDetail-actionButton"
                :iconClassName "ion-checkmark"
                :enabled changed})]]])))

(defcard
  devcard-note-detail*
  (om-root note-detail)
  {:className "devcards-noteDetail"
   :title "Title..."
   :body (clojure.string/join (repeat 500 "Body "))
   :changed false})

#_(defcard
  devcard-note-item*
  (om-root note-item)
  {:className "devcards-noteItem"})
