(ns mobile.lib.fetch
  (:require [re-frame.core :as rf]
            [mobile.components.core :refer [alert]]
            [cognitect.transit :as t]
            [clojure.string :as str]))

(rf/reg-fx
 :xhr
 (fn [{:keys [method uri params handler]}]
   (let [response (atom nil)
         w (t/writer :json)
         params (t/write w params)]
     (-> (js/fetch (str "http://localhost:3000" uri)
                   (clj->js {:redirect "manual"
                             :method method
                             :headers {"Content-Type" "application/json"}
                             :body (if (empty? params) nil params)}))
         (.then (fn [resp]
                  (reset! response resp)
                  (if resp.ok
                    (.text resp))))
         (.then
          (fn [response-body]
            (let [r (t/reader :json)]
              (handler (t/read r response-body))
              )))
         (.catch (fn [e]
                   (println method)
                   (println uri)
                   (println params)
                   (println "Fetch error" e.message))))
     {})))

