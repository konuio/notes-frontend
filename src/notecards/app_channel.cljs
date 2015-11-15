(ns notecards.app-channel
  (:require [cljs.core.async :refer [put!]]
            [notecards.app-state :as app-state]))

(defn post-set-page! [ch page]
  (put! ch {:action :set-page
            :page page}))

(defn handle-message! [data {:keys [action] :as message}]
  (case action
    :set-page (app-state/set-page! data (:page message))))
