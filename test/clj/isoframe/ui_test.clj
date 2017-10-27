(ns isoframe.ui-test
  (:require  [clojure.test :refer :all]
             [clojure.string :as str]

             [matcho.core :as matcho]

             [re-frame.core :as rf]
             [isoframe.events :as events]
             [isoframe.subs :as subs]

             [isoframe.test-utils :as u]))

(rf/reg-fx
 :xhr
 (fn [{:keys [method uri params handler]}]
   (let [uri (rest (str/split uri #"\/"))
         res (u/http (into [method] uri) {:body params})]
     (handler (:body res)))))

(defn dispatch [ev]
  (rf/dispatch ev)
  (Thread/sleep 100))

(deftest test-ui-logic
  (u/start-system)

  (def new-todo (u/http [:post "api" "todo"] {:body {:name "My todo"}}))

  (matcho/match
   new-todo
   {:status 201
    :body not-empty})

  (dispatch [:initialise-db])

  (is (= @(rf/subscribe [:showing]) "all"))

  (def todo-id (get-in new-todo [:body :id]))

  (matcho/match
   @(rf/subscribe [:todos])
   [{:name "My todo"}])

  (dispatch [:add-task {:todo-id todo-id :value "Prepare 4 ITGM" :status "active"}])

  (def task (->> (u/http [:get "api" "task" {:todo-id todo-id}]) :body first))

  (def task-id (:id task))

  (matcho/match
   @(rf/subscribe [:tasks todo-id])
   [task])

  (dispatch [:save-value task-id "new value"])

  (matcho/match
   @(rf/subscribe [:tasks todo-id])
   [{:value "new value"}])

  (matcho/match
   (u/http [:get "api" "task" task-id])
   {:status 200
    :body {:value "new value"}})

  (dispatch [:toggle-status task])

  (def task (first @(rf/subscribe [:tasks todo-id])))

  (matcho/match task {:status "done"})

  (dispatch [:toggle-status task])

  (matcho/match
   @(rf/subscribe [:tasks todo-id])
   [{:status "active"}])

  (dispatch [:update-task (assoc task :status "done")])

  (matcho/match
   (u/http [:get "api" "task" task-id])
   {:status 200
    :body {:status "done"}})

  (matcho/match
   @(rf/subscribe [:tasks todo-id])
   [{:status "done"}])

  (dispatch [:set-showing "done"])

  (is (= @(rf/subscribe [:showing]) "done"))

  (matcho/match
   @(rf/subscribe [:visible-tasks todo-id])
   [{:id task-id}])

  (dispatch [:set-showing "active"])

  (is (empty? @(rf/subscribe [:visible-tasks todo-id])))

  (dispatch [:set-showing "all"])

  (matcho/match
   @(rf/subscribe [:visible-tasks todo-id])
   [{:id task-id}])

  (dispatch [:delete-task todo-id task-id])

  (is (empty? @(rf/subscribe [:tasks task-id])))

  (matcho/match
   (u/http [:get "api" "task" {:todo-id todo-id}])
   {:status 200
    :body empty?})

  )
