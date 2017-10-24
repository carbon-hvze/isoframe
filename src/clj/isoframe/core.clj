(ns isoframe.core
  (:require [hikari-cp.core :as hikari]
            [io.pedestal.http :as http]
            [io.pedestal.http
             [body-params :as body-params]
             [ring-middlewares :as pedestal-middlewares]
             [route :as route]]
            [io.pedestal.interceptor :as ic]

            [isoframe.migration :as migration]
            [isoframe.app :as app]))

(defn start-db [db-name]
  {:datasource (hikari/make-datasource {:adapter "postgresql"
                                        :port-number 6200
                                        :server-name "localhost"
                                        :username "postgres"
                                        :password "postgres"
                                        :database-name (name db-name)})})

(defn close-db [{ds :datasource}]
  (hikari/close-datasource ds))

(defn insert-in-request
  [value]
  (ic/interceptor
   {:name ::insert-in-request
    :enter (fn [context] (assoc-in context [:request ::component] value))}))

(defn start-server [db port]
  (let [custom-interceptors [(insert-in-request db)
                             (body-params/body-params)
                             (pedestal-middlewares/multipart-params)
                             http/transit-json-body]]
    (-> {:env :dev
         ::http/routes #(route/expand-routes (deref #'app/routes))
         ::http/type :jetty
         ::http/port port
         ::http/join? false}
        http/default-interceptors
        http/dev-interceptors
        (update [::http/interceptors] #(into % custom-interceptors))
        http/create-server
        http/start)))

(defn stop [{:keys [db web]}]
  (close-db db)
  (http/stop web))

(defn start [{:keys [db-name port]}]
  (let [db (start-db db-name)
        _ (migration/migrate db)
        web (start-server db port)]
    {:db db
     :web web}))

(defn -main [] (prn "I am the main.."))
