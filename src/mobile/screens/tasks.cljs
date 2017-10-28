(ns mobile.screens.tasks
  (:require [mobile.components.core :as c]
            [re-frame.core :refer [subscribe]]))


(defn TasksScreen [{:keys [navigation]}]
  (let [todos @(subscribe :todos)]
    (println todos)
    (fn []
      [c/view
       [c/text "Test text"]])))
