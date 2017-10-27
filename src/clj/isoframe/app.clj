(ns isoframe.app
  (:require [clojure.tools.logging :as log]

            [io.pedestal.interceptor :as ic]
            [mpg.core :as mpg]

            [isoframe.api :as api]
            [isoframe.jwt :as jwt]
            [isoframe.db :as db]))

(mpg/patch)

(defn *http-error
  [context exc]
  (let [cause (.getCause exc)]
    (if-let [status (:isoframe/http-error (ex-data cause))]
      (do
        (if (< status 500)
          (log/warn cause "HTTP Error" (ex-data cause))
          (log/error cause "HTTP Error" (ex-data cause)))
        (assoc context
               :response {:status status
                          :body (let [data (:isoframe/data (ex-data cause))]
                                  (if (string? data) {:message data} {:errors data}))}))
      (throw exc))))

(def http-error (ic/interceptor {:name ::http-error :error *http-error}))

(defn error [status data]
  (throw (ex-info "http error" {:isoframe/http-error status
                                :isoframe/data data})))

(defn index [ctx]
  {:status 200
   :body "index page should be here"})

(defn *keywordize-rt [ctx]
  (update-in ctx [:request :path-params :resource-type] keyword))

(def keywordize-rt (ic/interceptor {:name ::keywordize-rt :enter *keywordize-rt}))

(defmulti forbidden?
  #(get-in % [:request :path-params :resource-type]))

(defmethod forbidden?
  :default [_] true)

(defmethod forbidden?
  :user [_] false)

(defn *auth-only [{{{db :db} :component
                    headers :headers}
                   :request
                   :as ctx}]
  (if-let [auth (get headers "authorization")]
    (let [[valid? {{user-id :user-id} :claims}] (jwt/verify-jwt (subs auth 7))]
      (if valid?
        (assoc-in ctx [:request :user] (db/read db :user user-id))
        (error 403 "Authorized only")))
    (if (forbidden? ctx)
      (error 403 "Authorized only")
      ctx)))

(def auth-only
  (ic/interceptor {:name ::auth-only :enter *auth-only}))

(defn *tx [{{{db :db} :component
             user :user} :request
            :as ctx}]
  (let [res {:user user}
        tx (db/create-transaction db res)]
    (assoc-in ctx [:request :tx] tx)))

(def tx (ic/interceptor {:name ::tx :enter *tx}))

;; pedestal does not support multimethods as interceptors by default

(extend-protocol ic/IntoInterceptor
  clojure.lang.MultiFn
  (-interceptor [t]
    (ic/interceptor {:enter (fn [ctx]
                              (assoc ctx :response (t (:request ctx))))})))

(def routes
  `[[["/" {:get index}
      ^:interceptors [http-error keywordize-rt #_auth-only]
      ["/api/:resource-type" {:get api/search
                              :post [:create ^:interceptors [tx] api/create]}
       ["/:id" {:get api/*read
                :put [:update ^:interceptors [tx] api/*update]
                :delete [:delete ^:interceptors [tx] api/delete]
                :patch [:patch ^:interceptors [tx] api/patch]}]]]]])
