(ns notecards.cards.fields
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]))

(defcard field
         (om-root (fn [data]
                    (om/component
                      (html [:div.FieldCards-field
                             [:label.FieldCards-label "Email"]
                             [:input.FieldCards-input {:type "text"
                                                          :defaultValue ""}]])))))
