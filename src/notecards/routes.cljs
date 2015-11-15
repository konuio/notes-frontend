(ns notecards.routes
  (:require [secretary.core :refer-macros [defroute]]
            [cljs.core.async :refer [put!]]))

(defn define-routes! [ch]
  (defroute home-path "/" []
            (put! ch {:action :set-page
                                         :page :home}))
  (defroute login-path "/login" []
            (put! ch {:action :set-page
                                         :page :login}))
  (defroute signup-path "/signup" []
            (put! ch {:action :set-page
                                         :page :signup})))
