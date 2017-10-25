(ns isoframe.api
  (:require [clojure.tools.logging :as log]
            [clj-jwt.core :as jwt]

            [isoframe.db :as db])
  (:import org.mindrot.jbcrypt.BCrypt
           java.util.UUID))

(defn ctx-to-resource-type [ctx]
  (get-in ctx [:path-params :resource-type]))

(defmulti search ctx-to-resource-type)

(defmethod search :default
  [{{:keys [db]} :component
    {:keys [resource-type]} :path-params
    :as ctx}]
  {:status 200
   :body (db/all db resource-type)})

(defmulti create ctx-to-resource-type)

(defmethod create
  :default
  [{{db :db} :component
    res :transit-params :as ctx}]
  {:status 201
   :body (db/create db (ctx-to-resource-type ctx) res)})

(defn make-password [password]
  (BCrypt/hashpw password (BCrypt/gensalt)))

(defn test-password [password hashed]
  (BCrypt/checkpw password hashed))

(def secret-key "hello")

(defn sign-jwt [val]
  (-> val
      jwt/jwt
      (jwt/sign :HS256 secret-key)
      jwt/to-str))

(defn verify-jwt [token]
  (try
    (let [parsed (jwt/str->jwt token)]
      (when (jwt/verify parsed :HS256 secret-key)
        [true parsed]))
    (catch Exception e
      (log/warn e "Error while JWT parsing and verifying"))))

(defmethod create
  :user
  [{{db :db} :component
    user :transit-params :as ctx}]
  (let [user (update user :password make-password)
        user (db/create db :user user)
        session (db/create db :session {:email (:email user)})
        jwt {:token (sign-jwt {:session-id (:id session)
                               :user-id (:id user)})}]
    {:status 201
     :body {:jwt jwt :user user}}))

(defmulti *read ctx-to-resource-type)

(defmethod *read
  :default
  [{{db :db} :component
    {id :id rt :resource-type} :path-params}]
  (if-let [res (db/read db rt id)]
    {:status 200
     :body res}
    {:status 404
     :body {:message (str "Resource " rt "/" id " not found")}}))

(defmulti *update ctx-to-resource-type)

(defmethod *update
  :default
  [{{db :db} :component
    {:keys [id resource-type]} :path-params
    resource :transit-params}]
  {:status 200
   :body (db/update db resource-type id resource)})

(defmulti patch ctx-to-resource-type)

(defn deep-merge [r1 r2]
  (merge-with
   (fn [prev cur]
     (cond
       (map? prev) (deep-merge prev cur)
       :else cur))
   r1 r2))

(defmethod patch
  :default
  [{{db :db} :component
    {id :id rt :resource-type} :path-params
    resource :transit-params}]
  (if-let [r (db/read db rt id)]
    {:status 200
     :body (db/update db rt id (deep-merge r resource))}
    {:status 404
     :body {:message (str "Resource " rt "/" id " not found")}}))

(defmulti delete ctx-to-resource-type)

(defmethod delete
  :default
  [{{db :db} :component
    {id :id rt :resource-type} :path-params}]
  (if-let [res (db/delete db rt id)]
    {:status 200
     :body res}
    {:status 404
     :body {:message (str "Resource " rt "/" id " not found")}}))
