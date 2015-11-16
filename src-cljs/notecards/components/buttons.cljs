(ns notecards.components.buttons
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]))

(defn button [{:keys [type content className enabledClassName on-click on-mouse-enter on-mouse-leave] :as data}]
  (let [enabled (get data :enabled true)
        type (or type "button")]
    (om/component
      (html [:button
             {:type type
              :className (js/classNames (if enabled enabledClassName) className)
              :on-click (if enabled on-click)
              :on-mouse-enter (if enabled on-mouse-enter)
              :on-mouse-leave (if enabled on-mouse-leave)}
             content]))))
