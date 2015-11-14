(ns notecards.omcards
  (:require-macros
    [devcards.core :refer [defcard defcard-om om-root]]
    )
  (:require [om.core :as om :include-macros true]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console :include-macros true]
            [goog.string.format]
            ))

(defn widget [{:keys [text] :as data} owner]
  (do
    (console/log "text:" text)
    (om/component
      (html [:div
             [:h1
              {:on-click (fn [_]
                           (do
                             (console/log "clicked...")
                             (om/transact! data :text #(goog.string.format "%s + new text..." %))
                             )
                           )}
              "om widget!!!!!"]
             [:div text]]))))

(defcard omcard-2
         (om-root widget)
         {:text "Changing the atom......."}

         ;; pprint the current atom.
         {:inspect-data true

          ;; enable time travel for the atom.
          ;; - the step back button appears once you have made some change to the app state.
          ;; - the step forward (and jump to current) buttons appear once you have moved back a bit in the history.
          ;; - Time travel is useful for stateful components.
          :history true

          ; watch-atom doesn't make sense to me.
          ;:watch-atom true
          })

; HOW can I get prop-based reloading?????
; it's the INITIAL data...
(defcard-om omcard-1
            widget
            {:text "hmmmmmmm, my new data for this root!!"})

(defonce myatom (atom {:text "more text..."}))

(defcard-om omcard widget myatom)
