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
 (fn [{:keys [method uri body handler]}]
   (let [uri (rest (str/split uri #"\/"))
         res (u/http (into [method] uri) {:body body})]
     (handler (:body res)))))

(deftest test-ui-logic
  (u/start-system)

  (def new-todo (u/http [:post "api" "todo"] {:body {:name "My todo"}}))

  (matcho/match
   new-todo
   {:status 201
    :body not-empty})

  (rf/dispatch [:initialise-db])

  (Thread/sleep 500)

  (is (= @(rf/subscribe [:showing]) :all))

  (def todo-id (get-in new-todo [:body :id]))

  (matcho/match
   @(rf/subscribe [:todos])
   [{:name "My todo"}])

  (rf/dispatch [:add-task {:todo-id todo-id :value "Prepare 4 ITGM" :status "active"}])

  (Thread/sleep 500)

  (def task (->> (u/http [:get "api" "task" {:todo-id todo-id}]) :body first))

  (def task-id (:id task))

  (matcho/match
   @(rf/subscribe [:tasks todo-id])
   [task])

  (rf/dispatch [:update-task (assoc task :status "done")])

  (Thread/sleep 500)

  (matcho/match
   (u/http [:get "api" "task" task-id])
   {:status 200
    :body {:status "done"}})

  (matcho/match
   @(rf/subscribe [:tasks todo-id])
   [{:status "done"}])

  (rf/dispatch [:set-showing "done"])

  (Thread/sleep 500)

  (is (= @(rf/subscribe [:showing]) "done"))

  (matcho/match
   @(rf/subscribe [:visible-tasks todo-id])
   [{:id task-id}])

  (rf/dispatch [:set-showing "active"])

  (Thread/sleep 500)

  (is (empty? @(rf/subscribe [:visible-tasks todo-id])))

  (rf/dispatch [:set-showing "all"])

  (Thread/sleep 500)

  (matcho/match
   @(rf/subscribe [:visible-tasks todo-id])
   [{:id task-id}])

  )
