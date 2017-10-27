(ns isoframe.server
  (:require [isoframe.core :as core]
            [isoframe.db :as db]))

(def sys (core/start {:db-name :isoframe :port 3000}))

(db/create (:db sys) :todo {:name "My todo list"})
