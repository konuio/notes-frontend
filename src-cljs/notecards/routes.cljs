(ns notecards.routes
  (:require [secretary.core :refer-macros [defroute]]
            [cljs.core.async :refer [put!]]))

(defn define-routes! [post-message]
  (defroute home-path "/" []
            (post-message {:action :set-page
                           :page :home}))
  (defroute login-path "/login" []
            (post-message {:action :set-page
                           :page :login}))
  (defroute signup-path "/signup" []
            (post-message {:action :set-page
                           :page :signup})))
