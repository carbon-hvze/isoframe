(ns isoframe.test-utils
  (:require [clojure.string :as str]

            [clojure.java.jdbc :as jdbc]
            [io.pedestal.test :as test]
            [cognitect.transit :as transit]
            [clj-jwt.core :as jwt]
            [io.pedestal.http :as http]

            [isoframe.core :as core]
            [isoframe.db :as db])
  (:import java.io.ByteArrayInputStream
           java.io.ByteArrayOutputStream))

(defonce system (atom nil))
(defonce auth (atom nil))

(defn truncate-db [db]
  (let [all-tables (->> {:select [:*]
                         :from [:information_schema.tables]
                         :where [:and
                                 [:in :table_schema ["public" "analytics"]]
                                 [:= :table_type "BASE TABLE"]]}
                        (db/query db))
        names (-> (into #{} (map (juxt :table_schema :table_name) all-tables))
                  (disj "schema_version"))]
    (doseq [[schema n] names]
      (jdbc/execute! db [(str "TRUNCATE TABLE \"" schema "\".\"" n "\" CASCADE")]))))

(defn clear-db []
  (when-let [s @system]
    (truncate-db (:db s))))

(defn stop-system []
  (when-let [sys @system] (core/stop sys)))

(defn start-system []
  (stop-system)
  (reset! system (core/start {:db-name :isoframe_test :port 3001}))
  (clear-db))

(defn user-id []
  (assert @auth "Authorization should not be nil")
  (:user-id @auth))

(def secret-key "hello")

(defn jwt [val]
  (-> val
      jwt/jwt
      (jwt/sign :HS256 secret-key)
      jwt/to-str))

#_(defn auth! []
  (let [db (:db @system)
        usr (db/create db :user {:name "User"
                                 :email "user@gmail.com"
                                 :password "pass123"})
        session (db/create db :session {:email (:email usr)})]
    (reset! auth {:session-id (:id session)
                  :user-id (:id usr)})))

#_(defn start-system-with-auth []
  (start-system)
  (auth!))

(defn read-transit [s]
  (let [in (ByteArrayInputStream. (.getBytes s "UTF-8"))]
    (transit/read (transit/reader in :json))))

(defn ->transit [s]
  (let [out (ByteArrayOutputStream.)
        writer (transit/writer out :json)]
    (transit/write writer s)
    (.toString out "UTF-8")))

(defn http [path & [opts]]
  (assert @system "System should be initialized")
  (let [verb (first path)
        q-p (if (map? (last path)) (last path) {})
        q-s (reduce (fn [s [k v]]
                      (str s
                           (let [n (str (name k) "=" v)]
                             (if (empty? s)
                               (str "?" n)
                               (str "&" n)))))
                    "" q-p)
        path (str/join "/" (if (map? (last path))
                             (butlast (rest path))
                             (rest path)))
        url  (str (if-not (str/starts-with? path "/") (str "/" path) path) q-s)
        pedestal-fn (get-in @system [:web :io.pedestal.http/service-fn])
        res (test/response-for pedestal-fn verb url
                               :headers (cond-> {"Content-Type" "application/transit+json"}
                                          @auth (assoc "Authorization" (str "Bearer " (jwt @auth))))
                               :body (->transit (:body opts)))
        read-transit (fn [res]
                       (let [tp (get-in res [:headers "Content-Type"])]
                         (if (and tp (str/includes? tp "transit"))
                           (update res :body read-transit)
                           res)))]
    (-> res
        read-transit
        (select-keys [:status :body]))))
