(ns isoframe.core
  (:require [hikari-cp.core :as hikari]

            [isoframe.migration :as migration]))

(defn start-db []
  {:datasource (hikari/make-datasource {:adapter "postgresql"
                                        :port-number 6200
                                        :server-name "localhost"
                                        :username "postgres"
                                        :password "postgres"
                                        :database-name "isoframe"})})

(defn close-db [{ds :datasource}]
  (hikari/close-datasource ds))

(defn -main
  []
  (let [db (start-db)]
    (migration/migrate db)
    (close-db db)))
