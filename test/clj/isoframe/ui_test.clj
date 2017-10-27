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
   {todo-id {:name "My todo"}})

  (def new-task {:todo-id todo-id :value "Prepare 4 ITGM"})

  (rf/dispatch [:add-task new-task])

  (Thread/sleep 500)

  (matcho/match
   (u/http [:get "api" "task" {:todo-id todo-id}])
   {:status 200
    :body [new-task]})

  (matcho/match
   @(rf/subscribe [:tasks todo-id])
   [new-task]))
