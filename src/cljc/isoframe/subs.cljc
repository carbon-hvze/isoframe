(ns isoframe.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :showing
 (fn [db _]
   (:showing db)))

(rf/reg-sub
 :todos
 (fn [db _]
   (or (vals (:todos db)) [])))

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

;; (rf/reg-sub
;;  :all-complete?
;;  :<- [:todos]
;;  (fn [todos _]
;;    (every? :done todos)))

;; (rf/reg-sub
;;  :completed-count
;;  :<- [:todos]
;;  (fn [todos _]
;;    (count (filter :done todos))))

;; (rf/reg-sub
;;  :footer-counts
;;  :<- [:todos]
;;  :<- [:completed-count]
;;  (fn [[todos completed] _]
;;    [(- (count todos) completed) completed]))
