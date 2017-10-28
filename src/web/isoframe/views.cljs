(ns isoframe.views
  (:require [reagent.core  :as reagent]
            [re-frame.core :as rf]
            [clojure.string :as str]))

(defn todo-input [{:keys [title on-save on-stop]}]
  (let [val  (reagent/atom title)
        stop #(do (reset! val "")
                  (when on-stop (on-stop)))
        save #(let [v (-> @val str str/trim)]
                (when (seq v) (on-save v))
                (stop))]
    (fn [props]
      [:input (merge props
                     {:type        "text"
                      :value       @val
                      :auto-focus  true
                      :on-blur     save
                      :on-change   #(reset! val (-> % .-target .-value))
                      :on-key-down #(case (.-which %)
                                      13 (save)
                                      27 (stop)
                                      nil)})])))

(defn task-input [todo-id]
  [:header#header
   [:h1 "todos"]
   [todo-input
    {:id "new-todo"
     :placeholder "What needs to be done?"
     :on-save #(rf/dispatch [:add-task {:todo-id todo-id :value % :status "active"}])}]])

(defn task-item []
  (let [editing (reagent/atom false)]
    (fn [{:keys [id status value] :as task}]
      (let [done? (= status "done")]
        [:li {:class (str (when done? "completed")
                          (when @editing "editing"))}
         [:div.view
          [:input.toggle
           {:type "checkbox"
            :checked done?
            :on-change #(rf/dispatch [:toggle-status task])}]
          [:label
           {:on-double-click #(reset! editing true)}
           value]
          [:button.destroy
           {:on-click #(rf/dispatch [:delete-task (:todo-id task) id])}]]
         (when @editing
           [todo-input
            {:class "edit"
             :title value
             :on-save #(rf/dispatch [:save-value id %])
             :on-stop #(reset! editing false)}])]))))

(defn task-list [todo-id]
  (let [visible-tasks @(rf/subscribe [:visible-tasks todo-id])]
    [:section#main
     [:ul#todo-list
      (for [task visible-tasks]
        ^{:key (:id task)} [task-item task])]]))

(defn todo-app
  []
  [:div
   (when-let [todo (first @(rf/subscribe [:todos]))]
     [:section#todoapp
      [task-input (:id todo)]
      [task-list (:id todo)]])
   [:footer#info
    [:p "Double-click to edit a todo"]]])
