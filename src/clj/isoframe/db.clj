(ns isoframe.db
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]

            [cheshire.core :as json]
            [honeysql.core :as hsql]
            [honeysql.helpers]
            [honeysql-postgres.format]
            [honeysql-postgres.helpers]))

(defn- honetize [hsql]
  (cond (map? hsql) (hsql/format hsql :quoting :ansi)
        (vector? hsql) (if (keyword? (first hsql)) (hsql/format (apply hsql/build hsql) :quoting :ansi) hsql)
        (string? hsql) [hsql]))

(defmacro from-start [start]
  `(Math/floor (/ (double (- (. java.lang.System nanoTime) ~start)) 1000000.0)))

(defn query
  ([db hsql]
   (let [sql (honetize hsql)
         start (. java.lang.System nanoTime)]
     (log/debug hsql)
     (try
       (let [res (jdbc/query db sql)]
         (log/info (str "[" (from-start start) "ms]") sql)
         res)
       (catch Exception e
         (log/error (str "[" (from-start start) "ms]") sql)
         (throw e))))))

(defn query-first [db & hsql]
  (first
   (apply query db hsql)))

(defn query-value [db & hsql]
  (when-let [row (apply query-first db hsql)]
    (first (vals row))))

(defn update-ts [{ts :ts :as rec}]
  (if ts (update rec :ts #(* (.toEpochSecond %) 1000)) rec))

(defn record-to-resource [rec]
  (when rec
    (-> (merge (:resource rec) (dissoc rec :resource))
        (update-ts))))

(defn to-table-name [rt]
  (keyword (str (name rt) "_resource")))

(defn all [db rt]
  (->> {:select [:*]
        :from [(to-table-name rt)]}
       (query db)
       (map record-to-resource)))

(defn create [db rt resource]
  (->> {:insert-into (to-table-name rt)
        :values [{:id (hsql/raw "gen_random_uuid()::text")
                  :resource (json/generate-string resource)}]
        :returning [:*]}
       (query-first db)
       (record-to-resource)))

(defn read [db rt id]
  (->> {:select [:*]
        :from [(to-table-name rt)]
        :where [:= :id id]}
       (query-first db)
       (record-to-resource)))

(defn update [db rt id resource]
  (->> {:update (to-table-name rt)
        :set {:resource (json/generate-string resource)}
        :where [:= :id id]
        :returning [:*]}
       (query-first db)
       (record-to-resource)))

(defn delete [db rt id]
  (->> {:delete-from (to-table-name rt)
        :where [:= :id id]
        :returning [:*]}
       (query-first db)
       (record-to-resource)))
