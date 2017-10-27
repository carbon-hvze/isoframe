(ns isoframe.api-test
  (:require  [clojure.test :refer :all]
             [clojure.string :as str]

             [matcho.core :as matcho]

             [isoframe.test-utils :as u]))

(deftest test-api
  (u/start-system)

  (matcho/match
   (u/http [:get "api" "todo"])
   {:status 200
    :body empty?})

  (def new-todo (u/http [:post "api" "todo"] {:body {:name "My todo list"}}))

  (def new-todo-id (get-in new-todo [:body :id]))

  (matcho/match new-todo {:status 201})

  (matcho/match
   (u/http [:get "api" "todo" new-todo-id])
   {:status 200 :body (:body new-todo)})

  (matcho/match
   (u/http [:get "api" "task"])
   {:status 400
    :body #"Query param"})

  (matcho/match
   (u/http [:get "api" "task" {:todo-id new-todo-id}])
   {:status 200
    :body empty?})

  (def new-task
    (u/http [:post "api" "task"] {:body {:todo-id new-todo-id :value "Pepper 4 ITGM"}}))

  (def new-task-id (get-in new-task [:body :id]))

  (matcho/match new-task {:status 201})

  (matcho/match
   (u/http [:get "api" "task" {:todo-id new-todo-id}])
   {:status 200
    :body [(:body new-task)]}))


