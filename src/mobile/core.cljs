(ns mobile.core
  (:require [reagent.core :as r]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [mobile.lib.fetch]
            [isoframe.events]
            [isoframe.subs]
            [mobile.components.core :as c]
            [mobile.screens.todos :refer [TodosScreen]]
            [mobile.screens.tasks :refer [TasksScreen]]))

(def routes
  (r/adapt-react-class
   (c/stack-navigator
    {"Todos"
     {:screen (r/reactify-component TodosScreen)
      :navigationOptions {:title "Todos"}}
     "Tasks"
     {:screen (r/reactify-component TasksScreen)
      :navigationOptions {:title "Tasks"}}}
    {:initialRouteName "Todos"})))

(defn app-root []
  (fn []
    [routes]))

(defn init []
  (dispatch-sync [:initialise-db])
  (.registerComponent c/app-registry "TestApp" #(r/reactify-component app-root)))
