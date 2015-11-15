(ns notecards.buttons
  (:require [om.core :as om]
            [sablono.core :refer-macros [html]]))

(defn button [{:keys [type content className enabledClassName on-click] :as data}]
  (let [enabled (get data :enabled true)
        type (or type "button")]
    (om/component
      (html [:button
             {:type type
              :className (js/classNames (if enabled enabledClassName) className)
              :on-click (if enabled on-click)}
             content]))))
