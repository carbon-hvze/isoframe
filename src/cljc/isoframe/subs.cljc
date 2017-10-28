(ns isoframe.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :showing
 (fn [db _]
   (:showing db)))

(rf/reg-sub
 :todos
 (fn [db _]
   (if-let [todos (not-empty (:todos db))]
     (->> todos vals (sort-by :ts))
     [])))

(rf/reg-sub
 :tasks
 (fn [db [_ todo-id]]
   (vals (get-in db [:todos todo-id :tasks]))))

(rf/reg-sub
 :visible-tasks
 (fn [[_ todo-id]]
   [(rf/subscribe [:tasks todo-id])
    (rf/subscribe [:showing])])
 (fn [[tasks showing] _]
   (let [filter-fn (if (= showing "all")
                     identity
                     #(= (:status %) showing))]
   (filter filter-fn tasks))))
