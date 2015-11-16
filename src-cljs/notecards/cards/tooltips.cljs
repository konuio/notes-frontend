(ns notecards.cards.tooltips
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]
            [notecards.components.tooltips :refer [tooltip]]))

(defcard tooltip-card
         (om-root tooltip)
         {:className "TooltipCards-tooltip"
          :content "Tooltip"})
