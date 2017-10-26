(ns isoframe.events
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as rf]
            [isoframe.db :as db]))

(defn check-and-throw
  [a-spec db]
  (when-not (s/valid? a-spec db)
    (throw (ex-info (str "spec check failed: " (s/explain-str a-spec db)) {}))))

(def check-spec-interceptor
  (rf/after (partial check-and-throw ::db/db)))

(def todo-interceptors [check-spec-interceptor
                        (rf/path :todos)
                        (rf/after db/todos->local-store)
                        rf/trim-v])

(defn allocate-next-id [todos]
  ((fnil inc 0) (last (keys todos))))

(rf/reg-event-fx
 :initialise-db
  [(rf/inject-cofx :local-store-todos)
   check-spec-interceptor]
  (fn [{:keys [db local-store-todos]} _]
    {:db (assoc db/default-db :todos local-store-todos)}))

(rf/reg-event-db
  :set-showing
  [check-spec-interceptor]
  (fn [db [_ new-filter-kw]]
    (assoc db :showing new-filter-kw)))

(rf/reg-event-db
  :add-todo
  todo-interceptors
  (fn [todos [text]]
    (let [id (allocate-next-id todos)]
      (assoc todos id {:id id :title text :done false}))))

(rf/reg-event-db
  :toggle-done
  todo-interceptors
  (fn [todos [id]]
    (update-in todos [id :done] not)))

(rf/reg-event-db
  :save
  todo-interceptors
  (fn [todos [id title]]
    (assoc-in todos [id :title] title)))

(rf/reg-event-db
  :delete-todo
  todo-interceptors
  (fn [todos [id]]
    (dissoc todos id)))

(rf/reg-event-db
  :clear-completed
  todo-interceptors
  (fn [todos _]
    (->> (vals todos)
         (filter :done)
         (map :id)
         (reduce dissoc todos))))

(rf/reg-event-db
  :complete-all-toggle
  todo-interceptors
  (fn [todos _]
    (let [new-done (not-every? :done (vals todos))]
      (reduce #(assoc-in %1 [%2 :done] new-done)
              todos
              (keys todos)))))
