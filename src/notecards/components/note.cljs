(ns notecards.note
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [shodan.console :as console :include-macros true]
            [goog.string.format]
            [clojure.string]
            [notecards.button :refer [button]]))

(defn note-item [{:keys [title body selected className]}]
  (om/component
    (html [:div
           {:className (js/classNames "noteItem" (if selected "noteItem--selected") className)}
           [:div.noteItem-title title]
           [:div.noteItem-body (subs body 0 (min (count body) 100))]])))

(defcard
  devcard-note-item
  (om-root note-item)
  {:className "devcards-noteItem"
   :title "Title..."
   :body (clojure.string/join (repeat 500 "Body "))})

(defn note-items [{:keys [notes className]}]
  (om/component
    (html [:div
           {:className (js/classNames "noteItems" className)}
           [:div.noteItems-items
            (map-indexed
              (fn [i note]
                (om/build
                  note-item
                  (merge note {:react-key i
                               :className "noteItems-item"})))
              notes)]
           [:div.noteItems-actions
            (om/build
              button
              {:className "noteItems-actionButton"
               :iconClassName "ion-plus"})]])))

(defcard
  devcard-note-items*
  (om-root note-items)
  {:className "devcards-noteItems"
   :notes (into []
                (map (fn [i]
                       {:title (goog.string.format "Title %d" i)
                        :body (goog.string.format "Body %d" i)}))
                (range 20))})

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
