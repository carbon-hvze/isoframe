(ns isoframe.jwt
  (:require [clj-jwt.core :as jwt]
            [clojure.tools.logging :as log]))

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

