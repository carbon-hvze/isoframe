(ns isoframe.events
  (:require [re-frame.core :as rf]))

(def todo-interceptors [(rf/path :todos) rf/trim-v])

(rf/reg-event-fx
 :initialise-db
 (fn [cofx ev]
   {:db {:todos {}
         :showing "all"}
    :xhr {:method :get
          :uri "/api/todo"
          :handler #(rf/dispatch [:save-todos %])}}))

(rf/reg-event-db
 :save-todos
 [rf/trim-v]
 (fn [db [todos]]
   (reduce #(assoc-in %1 [:todos (:id %2)] %2) db todos)))

(rf/reg-event-db
 :save-task
 [rf/trim-v]
 (fn [db [task]]
   (assoc-in db [:todos (:todo-id task) :tasks (:id task)] task)))

(rf/reg-event-fx
 :add-task
 (fn [db [_ task]]
   {:xhr {:method :post
          :uri "/api/task"
          :params task
          :handler #(rf/dispatch [:save-task %])}}))

(rf/reg-event-fx
 :update-task
 (fn [db [_ task]]
   {:xhr {:method :put
          :uri (str "/api/task/" (:id task))
          :params task
          :handler #(rf/dispatch [:save-task %])}}))

(rf/reg-event-db
  :set-showing
  (fn [db [_ new-filter-kw]]
    (assoc db :showing new-filter-kw)))

(rf/reg-event-fx
  :toggle-status
  (fn [db [_ task]]
    (let [new-task (update task :status #(if (= % "done") "active" "done"))]
      {:dispatch [:update-task new-task]})))

(rf/reg-event-db
 :delete-task-db
 (fn [db [_ todo-id task-id]]
   (update-in db [:todos todo-id :tasks] dissoc task-id)))

(rf/reg-event-fx
 :delete-task
 (fn [_ [_ todo-id task-id]]
   {:xhr {:method :delete
          :uri (str "/api/task/" task-id)
          :handler #(rf/dispatch [:delete-task-db todo-id task-id])}}))

(rf/reg-event-fx
 :save-value
 (fn [_ [_ task-id value]]
   {:xhr {:method :patch
          :uri (str "/api/task/" task-id)
          :params {:value value}
          :handler #(rf/dispatch [:save-task %])}}))
