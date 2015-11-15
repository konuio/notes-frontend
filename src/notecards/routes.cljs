(ns notecards.routes
  (:require [secretary.core :refer-macros [defroute]]
            [notecards.app-state :as app-state]))

(defn define-routes! [ch]
  (defroute home-path "/" []
            (app-state/post-message! ch {:action :set-page
                                         :page :home}))
  (defroute login-path "/login" []
            (app-state/post-message! ch {:action :set-page
                                         :page :login}))
  (defroute signup-path "/signup" []
            (app-state/post-message! ch {:action :set-page
                                         :page :signup})))
