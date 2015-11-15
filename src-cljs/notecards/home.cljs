(ns notecards.home
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.app-state :as app-state]
            [notecards.buttons :refer [button]]
            [notecards.history :as history]
            [notecards.routes :as routes]
            [notecards.utils :refer [truncate find-first]]))

(defn error-message [{:keys [className title subtitle]}]
  (om/component
    (html [:div
           {:className (js/classNames "ErrorMessage" className)}
           [:div.ErrorMessage-title title]
           [:div.ErrorMessage-subtitle subtitle]])))

(defn notes-view [{:keys [className ch notes selected loading]}]
  (let [createable (not loading)]
    (om/component
      (html [:div
             {:className (js/classNames "Notes" className)}
             (if (seq notes)
               [:div.Notes-notes
                (map-indexed (fn [i note]
                               (om/build
                                 (fn [note]
                                   (om/component
                                     (html [:div
                                            {:className (js/classNames "Notes-note" (if (= selected (:id note)) "Notes-note--selected"))
                                             :on-click (fn [e]
                                                         (app-state/post-message! ch {:action :select-note
                                                                                      :id     (:id note)}))}
                                            [:div.Notes-title (if (empty? (:title note))
                                                                "Untitled"
                                                                (:title note))]
                                            [:div.Notes-body (truncate (:data note) 100)]])))
                                 note
                                 {:react-key i}))
                             notes)]
               [:div.Notes-empty
                (om/build
                  error-message
                  {:title "No notes"
                   :subtitle "Press the + to create your first note."})])
             [:div.Notes-actions
              (om/build button
                        {:className "Notes-action"
                         :enabledClassName "Notes-action--enabled"
                         :enabled createable
                         :content [:i.Notes-actionIcon.icon.ion-plus]
                         :on-click (fn [e]
                                     (app-state/post-message! ch {:action :create-note}))})]]))))

(defn note-view [{:keys [className ch note pending loading]}]
  (let [working-note (merge note pending)
        is-pending (seq pending)
        deletable (not loading)
        editable (and (not loading) is-pending)]
    (om/component
      (html [:div
             {:className (js/classNames "Note" className)}
             (if note
               [:div.Note-note
                [:div.Note-title
                 [:input.Note-titleInput
                  {:type "text"
                   :value (:title working-note)
                   :placeholder "Title"
                   :spellCheck false
                   :on-change (fn [e]
                                (app-state/post-message! ch {:action :set-pending-note
                                                             :note   (assoc working-note :title e.currentTarget.value)}))}]
                 (if is-pending [:div.Note-pending])]
                [:textarea.Note-body
                 {:value (:data working-note)
                  :placeholder "Body"
                  :spellCheck false
                  :on-change (fn [e]
                               (app-state/post-message! ch {:action :set-pending-note
                                                            :note   (assoc working-note :data e.currentTarget.value)}))}]
                [:div.Note-actions
                 [:div.Note-actionGroup
                  (om/build button
                            {:className "Note-action"
                             :enabledClassName "Note-action--enabled"
                             :enabled deletable
                             :content [:i.Note-actionIcon.icon.ion-trash-b]
                             :on-click (fn [e]
                                         (app-state/post-message! ch {:action :delete-note
                                                                      :id     (:id working-note)}))})]
                 [:div.Note-actionGroup
                  (om/build button
                            {:className "Note-action"
                             :enabledClassName "Note-action--enabled"
                             :enabled editable
                             :content [:i.Note-actionIcon.icon.ion-close]
                             :on-click (fn [e]
                                         (app-state/post-message! ch {:action :cancel-note}))})
                  (om/build button
                            {:className "Note-action"
                             :enabledClassName "Note-action--enabled"
                             :enabled editable
                             :content [:i.Note-actionIcon.icon.ion-checkmark]
                             :on-click (fn [e]
                                         (app-state/post-message! ch {:action :save-note
                                                                      :note   working-note}))})]]]
               [:div.Note-empty
                (om/build
                  error-message
                  {:className "Note-empty"
                   :title "No note selected"
                   :subtitle "Select a note on the left to get started."})])]))))

(defn home-page [{:keys [className ch notes home]}]
  (let [{:keys [selected pending loading]} home]
    (reify
      om/IWillMount
      (will-mount [_]
        (app-state/post-message! ch {:action :get-notes}))
      om/IRender
      (render [_]
        (html [:div
               {:className (js/classNames "HomePage" className)}
               [:div.HomePage-navbar
                (om/build button
                          {:className "HomePage-button"
                           :content "Log out"
                           :on-click (fn [e]
                                       (.preventDefault e)
                                       (app-state/post-message! ch {:action :log-out}))})]
               [:div.HomePage-card
                (om/build notes-view {:className "HomePage-column"
                                      :ch ch
                                      :notes notes
                                      :selected selected
                                      :loading loading})
                (if (seq notes)
                  (om/build note-view {:className "HomePage-column"
                                       :ch ch
                                       :note (find-first #(= (:id %) selected) notes)
                                       :pending pending
                                       :loading loading}))]])))))
