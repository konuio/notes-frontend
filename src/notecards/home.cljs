(ns notecards.home
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.app-state :as app-state]
            [notecards.truncate :refer [truncate]]))

(defn notes-view [{:keys [className ch notes selected]}]
  (om/component
    (html [:div
           {:className (js/classNames "Notes" className)}
           (map-indexed (fn [i note]
                          (om/build
                            (fn [note]
                              (om/component
                                (html [:div
                                       {:className (js/classNames "Notes-note" (if (= selected (:id note)) "Notes-note--selected"))
                                        :on-click (fn [e]
                                                    (app-state/post-message! ch {:action :select-note
                                                                                 :id (:id note)}))}
                                       [:div.Notes-title (:title note)]
                                       [:div.Notes-body (truncate (:data note) 100)]])))
                            note
                            {:react-key i}))
                        notes)])))

(defn note-view [{:keys [className ch note]}]
  (om/component
    (html (if note
            [:div
             {:className (js/classNames "Note" className)}
             [:div.Note-title (:title note)]
             [:div.Note-body (:data note)]]
            [:div
             {:className (js/classNames "Note Note--empty" className)}
             [:div.Note-empty
              [:div.Note-emptyTitle "No note selected"]
              [:div.Note-emptyBody "Select a note on the left to get started."]]]))))

(defn home-page [{:keys [className ch notes selected-note]}]
  (reify
    om/IWillMount
    (will-mount [_]
      (app-state/post-message! ch {:action :get-notes}))
    om/IRender
    (render [_]
      (html [:div
             {:className (js/classNames "HomePage" className)}
             [:div.HomePage-navbar
              [:button.HomePage-button
               {:type "button"
                :on-click (fn [e]
                            (.preventDefault e)
                            (app-state/post-message! ch {:action :log-out}))}
               "Log out"]]
             [:div.HomePage-card
              (om/build notes-view {:className "HomePage-column"
                                    :ch ch
                                    :notes notes
                                    :selected selected-note})
              (om/build note-view {:className "HomePage-column"
                                   :ch ch
                                   :note (first (filter #(= (:id %) selected-note) notes))})]]))))
