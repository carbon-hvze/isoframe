(ns isoframe.core
  (:require [goog.events :as events]
            [reagent.core :as reagent]
            [re-frame.core :as rf]
            [secretary.core :as secretary :refer-macros [defroute]]

            [isoframe.events]
            [isoframe.subs]
            [isoframe.views :as views])
  (:import [goog History]
           [goog.history EventType]))

(defroute "/" [] (rf/dispatch [:set-showing :all]))
(defroute "/:filter" [filter] (rf/dispatch [:set-showing (keyword filter)]))

(def history
  (doto (History.)
    (events/listen EventType.NAVIGATE
                   (fn [event] (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn init!
  []
  (rf/dispatch-sync [:initialise-db])
  (reagent/render [views/todo-app]
                  (.getElementById js/document "app")))

