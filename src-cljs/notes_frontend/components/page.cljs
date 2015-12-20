(ns notes-frontend.components.page
  (:require [goog.string.format]))

(defn page-background-image [static-url]
  (goog.string.format "url(%s/img/background.png)" static-url))
