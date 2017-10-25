(ns isoframe.api-test
  (:require  [clojure.test :refer :all]

             [matcho.core :as matcho]

             [isoframe.test-utils :as u]))

(defn init [f]
  (u/start-system)
  (f)
  (u/stop-system))

(use-fixtures :each init)

(deftest test-auth

  (matcho/match
   (u/http [:get "api" "todo"])
   {:status 401
    :body #"Unauthorized"})

  (matcho/match
   (u/http [:post "api" "todo"]
           {:body {:name "My todo" :tasks []}})
   {:status 401
    :body #"Unauthorized"})

  (def new-user
    (u/http [:post "api" "user"] {:body {:first-name "Tim" :last-name "Zallin" :password "123"}}))

  (def jwt (get-in new-user [:body :jwt]))

  (matcho/match
   new-user
   {:status 201
    :body {:jwt not-empty :user not-empty}})

  (matcho/match
   (u/http [:get "api" "todo"]
           {:jwt jwt})
   {:status 200
    :body empty?})

  )
