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
   {:status 403
    :body {:message #"Authorized only"}})

  (matcho/match
   (u/http [:get "api" "todo"] {:jwt "wrong jwt"})
   {:status 403
    :body {:message #"Authorized only"}})

  (matcho/match
   (u/http [:post "api" "todo"]
           {:body {:name "My todo" :tasks []}})
   {:status 403
    :body {:message #"Authorized only"}})

  (def new-user
    (u/http [:post "api" "user"] {:body {:first-name "Tim" :last-name "Zallin" :password "123"}}))

  (def jwt (get-in new-user [:body :jwt :token]))

  (matcho/match
   new-user
   {:status 201
    :body {:jwt not-empty :user not-empty}})

  (matcho/match
   (u/http [:get "api" "todo"] {:jwt jwt})
   {:status 200
    :body empty?})

  (matcho/match
   (u/http [:delete "api" "session"] {:jwt jwt})
   )

  )
