(ns notes-frontend.routes
  (:require [secretary.core :refer-macros [defroute]]
            [cljs.core.async :refer [put!]]
            [shodan.console :as console]))

(defn define-routes! [post-message]
  (defroute home-path "/" []
            (post-message {:action :set-page
                           :page :home}))
  (defroute login-path "/login" []
            (post-message {:action :set-page
                           :page :login}))
  (defroute signup-path "/signup" []
            (post-message {:action :set-page
                           :page :signup}))
  (defroute signup-pending-path "/signup-pending" []
            (post-message {:action :set-page
                           :page :signup-pending}))
  (defroute redeem-signup-path "/redeem-signup" [query-params]
            (let [{:keys [token]} query-params]
              (post-message {:action :set-page
                             :page :redeem-signup
                             :token token}))))
