(ns mobile.screens.todos
  (:require [mobile.components.core :as c]
            [reagent.core :as r]
            [clojure.string :as str]
            [re-frame.core :refer [subscribe dispatch]]))


(defn task-list [todo-id]
  (let [tasks @(subscribe [:visible-tasks todo-id])]
    [c/list-component
     (if (empty? tasks)
       [c/list-item {:style {:margin-left 20 :margin-top 20}}
        [c/text "Tasks not found"]]
       (for [item tasks]
         (do (println item)
             [c/list-item {:key (:id item)}
              [c/text (:value item)]])
         ))]))

(defn task-input [todo-id on-save]
  (let [val (r/atom "")
        input (r/atom nil)
        stop #(do (reset! val "")
                  (.setNativeProps @input #js{:text ""}))
        save #(let [v (-> @val str str/trim)]
                (when (seq v) (on-save v))
                (stop))]
    [c/form
     [c/item {:style {:margin-left 0}}
      [c/input {:placeholder "What needs to be done?"
                :ref #(reset! input %)
                :on-change-text #(reset! val %)
                :return-key-type "go"
                :on-blur save
                :on-submit-editing save
                }]]]))

(defn TodosScreen [{:keys [navigation]}]
  (let [todo (first @(subscribe [:todos]))
        todo-id (:id todo)]
    (fn []
      [c/container
       [c/content
        [task-input todo-id #(dispatch [:add-task {:todo-id todo-id :value % :status "active"}])]
        [task-list todo-id]]
       ])
    ))

