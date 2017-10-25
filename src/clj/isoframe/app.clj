(ns isoframe.app
  (:require [clojure.tools.logging :as log]

            [io.pedestal.interceptor :as ic]
            [mpg.core :as mpg]

            [isoframe.api :as api])
  )

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

(defn *auth-only [ctx]
  (prn "BITCH ME LIKE")
  (prn (keys (get-in ctx [:request :component])))
  ctx)

(def auth-only
  (ic/interceptor {:name ::auth-only
                   :enter *auth-only}))

;; pedestal does not support multimethods as handlers by default

(extend-protocol ic/IntoInterceptor
  clojure.lang.MultiFn
  (-interceptor [t]
    (ic/interceptor {:enter (fn [ctx]
                              (assoc ctx :response (t (:request ctx))))})))

(def routes
  `[[["/" {:get index}
      ^:interceptors [http-error keywordize-rt auth-only]
      ["/api/:resource-type" {:get api/search
                              :post api/create}
       ["/:id" {:get api/*read
                :put api/*update
                :delete api/delete
                :patch api/patch}]]]]])
