(ns isoframe.db
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.tools.logging :as log]
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
