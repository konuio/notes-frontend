(ns notecards.components.tooltips
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]
            [shodan.console :as console]))

(defn tooltip [{:keys [content target-rect className]} owner]
  (let [set-position (fn []
                       (let [node (om/get-node owner)
                             node-rect (.getBoundingClientRect node)
                             arrow-size 6]
                         (.css (js/$ node) (clj->js {:top (- target-rect.top (+ node-rect.height arrow-size))
                                                     :left (- (+ target-rect.left (/ target-rect.width 2)) (/ node-rect.width 2))}))))]
    (reify
      om/IDidMount
      (did-mount [_]
        (set-position))
      om/IDidUpdate
      (did-update [_ _ _]
        (set-position))
      om/IRender
      (render [_]
        (html [:div
               {:className (js/classNames "Tooltip" className)}
               content
               [:svg
                {:className "Tooltip-arrow"
                 :viewBox "0 0 12 6"}
                [:polygon {:points "0,0 6,6 12,0"}]]])))))
