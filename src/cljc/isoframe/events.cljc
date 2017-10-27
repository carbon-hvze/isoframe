(ns isoframe.events
  (:require #_[cljs.spec.alpha :as s]
            [re-frame.core :as rf]

            #_[isoframe.db :as db]))

#_(defn check-and-throw
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

;; (def check-spec-interceptor
;;   (rf/after (partial check-and-throw ::db/db)))

(def todo-interceptors [
                        ;;check-spec-interceptor
                        (rf/path :todos)
                        rf/trim-v])

(rf/reg-event-db
 :save-todos
 [rf/trim-v]
 (fn [db [todos]]
   (reduce #(assoc-in %1 [:todos (:id %2)] %2) db todos)))

(rf/reg-event-fx
 :initialise-db
 [#_check-spec-interceptor]
 (fn [cofx ev]
   {:db {:todos {}
         :showing :all}
    :xhr {:method :get
          :uri "/api/todo"
          :handler #(rf/dispatch [:save-todos %])}}))

(rf/reg-event-db
 :save-task
 [rf/trim-v]
 (fn [db [task]]
   (update-in db [:todos (:todo-id task) :tasks] conj task)))

(rf/reg-event-fx
 :add-task
 (fn [db [_ task]]
   {:xhr {:method :post
          :uri "/api/task"
          :body task
          :handler #(rf/dispatch [:save-task %])}}))

;; (rf/reg-event-db
;;   :set-showing
;;   [check-spec-interceptor]
;;   (fn [db [_ new-filter-kw]]
;;     (assoc db :showing new-filter-kw)))

;; (rf/reg-event-db
;;   :toggle-done
;;   todo-interceptors
;;   (fn [todos [id]]
;;     (update-in todos [id :done] not)))

;; (rf/reg-event-db
;;   :save
;;   todo-interceptors
;;   (fn [todos [id title]]
;;     (assoc-in todos [id :title] title)))

;; (rf/reg-event-db
;;   :delete-todo
;;   todo-interceptors
;;   (fn [todos [id]]
;;     (dissoc todos id)))

;; (rf/reg-event-db
;;   :clear-completed
;;   todo-interceptors
;;   (fn [todos _]
;;     (->> (vals todos)
;;          (filter :done)
;;          (map :id)
;;          (reduce dissoc todos))))

;; (rf/reg-event-db
;;   :complete-all-toggle
;;   todo-interceptors
;;   (fn [todos _]
;;     (let [new-done (not-every? :done (vals todos))]
;;       (reduce #(assoc-in %1 [%2 :done] new-done)
;;               todos
;;               (keys todos)))))
