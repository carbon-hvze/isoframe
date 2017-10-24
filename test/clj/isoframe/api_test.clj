(ns isoframe.api-test
  (:require  [clojure.test :refer :all]

             [matcho.core :as matcho]

             [isoframe.test-utils :as u]))

(defn init [f]
  (u/start-system)
  (f)
  (u/stop-system))

(use-fixtures :each init)

(deftest test-api

  (matcho/match
   (u/http [:get "/"])
   {:status 200})

  )

