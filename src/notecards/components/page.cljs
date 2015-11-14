(ns notecards.page
  (:require-macros [devcards.core :refer [defcard om-root]])
  (:require [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [shodan.console :as console :include-macros true]
            [goog.string.format]
            [clojure.string]))

(defn truncate [s]
  (let [max 100
        ellipsis "..."]
    (if (< (count s) max)
      s
      (str
        (subs s 0 (- max (count ellipsis)))
        ellipsis))))

(defn note-item [{:keys [title body className]}]
  (om/component
    (html [:div
           {:className (js/classNames "noteItem" className)}
           [:div.noteItem-header title]
           [:div.noteItem-body (truncate body)]])))

(defn note-detail [{:keys [title body className]}]
  (om/component
    (html [:div
           {:className (js/classNames "noteDetail" className)}
           [:input.noteDetail-title {:type "text"
                                     :defaultValue title}]
           [:textarea.noteDetail-body {:defaultValue body}]])))

(defn page [{:keys [className]}]
  (om/component
    (html [:div
           {:className (js/classNames "page" className)}
           [:div.page-navbar
            [:div.page-breadcrumb
             "Home"]]
           [:div.page-content
            [:div.page-card
             [:div.page-notes
              (map
                (fn [i]
                  (om/build
                    note-item
                    {:title (goog.string.format "Title %d" i)
                     :body "Body..."
                     :react-key i
                     :className "page-noteItem"}))
                (range 20))]
             (om/build note-detail {:title "Title!!!"
                                    :body (clojure.string/join (repeat 200 "Body!!! "))
                                    :className "page-noteDetail"})
             [:div.page-divider]]]])))

(defcard
  page-example*
  (om-root page)
  {:className "devcards-page"}
  {:inspect-data true})
