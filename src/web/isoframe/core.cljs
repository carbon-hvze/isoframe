(ns isoframe.core
  (:require [goog.events :as events]
            [reagent.core :as reagent]
            [re-frame.core :as rf]

            [isoframe.events]
            [isoframe.subs]
            [isoframe.views]))

(defn ^:export main
  []
  (dispatch-sync [:initialise-db])
  (reagent/render [todomvc.views/todo-app]
                  (.getElementById js/document "app")))

