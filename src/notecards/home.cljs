(ns notecards.home
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.app-state :as app-state]
            [notecards.utils :refer [truncate find-first]]))

(defn notes-view [{:keys [className ch notes selected]}]
  (om/component
    (html [:div
           {:className (js/classNames "Notes" className)}
           [:div.Notes-notes
            (map-indexed (fn [i note]
                           (om/build
                             (fn [note]
                               (om/component
                                 (html [:div
                                        {:className (js/classNames "Notes-note" (if (= selected (:id note)) "Notes-note--selected"))
                                         :on-click (fn [e]
                                                     (app-state/post-message! ch {:action :select-note
                                                                                  :id (:id note)}))}
                                        [:div.Notes-title (if (empty? (:title note))
                                                            "Empty"
                                                            (:title note))]
                                        [:div.Notes-body (truncate (:data note) 100)]])))
                             note
                             {:react-key i}))
                         notes)]
           [:div.Notes-actions
            [:button.Notes-action
             {:type "button"
              :on-click (fn [e]
                          (app-state/post-message! ch {:action :create-note}))}
             [:i.Notes-actionIcon.icon.ion-plus]]]])))

(defn note-view [{:keys [className ch note pending-note]}]
  (let [working-note (merge note pending-note)
        pending (seq pending-note)]
    (om/component
      (html (if note
              [:div
               {:className (js/classNames "Note" className)}
               [:input.Note-title
                {:type "text"
                 :value (:title working-note)
                 :placeholder "Title"
                 :on-change (fn [e]
                              (app-state/post-message! ch {:action :set-pending-note
                                                           :note (assoc working-note :title e.currentTarget.value)}))}]
               [:textarea.Note-body
                {:value (:data working-note)
                 :placeholder "Body"
                 :on-change (fn [e]
                              (app-state/post-message! ch {:action :set-pending-note
                                                           :note (assoc working-note :data e.currentTarget.value)}))}]
               [:div.Note-actions
                [:div.Note-actionGroup
                 [:button.Note-action.Note-action--enabled
                  {:type "button"
                   :on-click (fn [e]
                               (app-state/post-message! ch {:action :delete-note
                                                            :id (:id working-note)}))}
                  [:i.Note-actionIcon.icon.ion-trash-b]]]
                [:div.Note-actionGroup
                 [:button
                  {:type "button"
                   :className (js/classNames "Note-action" (if pending "Note-action--enabled"))
                   :on-click (if pending
                               (fn [e]
                                 (app-state/post-message! ch {:action :cancel-note})))}
                  [:i.Note-actionIcon.icon.ion-close]]
                 [:button
                  {:type "button"
                   :className (js/classNames "Note-action" (if pending "Note-action--enabled"))
                   :on-click (if pending
                               (fn [e]
                                 (app-state/post-message! ch {:action :save-note
                                                              :note working-note})))}
                  [:i.Note-actionIcon.icon.ion-checkmark]]]]
               (if pending [:div.Note-pending])]
              [:div
               {:className (js/classNames "Note Note--empty" className)}
               [:div.Note-empty
                [:div.Note-emptyTitle "No note selected"]
                [:div.Note-emptyBody "Select a note on the left to get started."]]])))))

(defn home-page [{:keys [className ch notes selected-note pending-note]}]
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
                                   :note (find-first #(= (:id %) selected-note) notes)
                                   :pending-note pending-note})]]))))
