(ns notecards.devcards.fields
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]))

(defcard field
         (om-root (fn [data]
                    (om/component
                      (html [:div.FieldDevcards-field
                             [:label.FieldDevcards-label "Email"]
                             [:input.FieldDevcards-input {:type "text"
                                                          :defaultValue ""}]])))))
