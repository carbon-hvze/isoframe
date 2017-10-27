(ns isoframe.core
  (:require [goog.events :as events]
            [reagent.core :as reagent]
            [re-frame.core :as rf]

            [isoframe.xhr]
            [isoframe.events]
            [isoframe.subs]
            [isoframe.views :as views])
  (:import [goog History]
           [goog.history EventType]))

(defn init!
  []
  (rf/dispatch-sync [:initialise-db])
  (reagent/render [views/todo-app]
                  (.getElementById js/document "app")))

