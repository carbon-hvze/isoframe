(ns isoframe.xhr
  (:require [ajax.core :as ajax]
            [re-frame.core :as rf]))

(def xhr-mapper
  {:get ajax/GET
   :post ajax/POST
   :put ajax/PUT
   :patch ajax/PATCH
   :delete ajax/DELETE})

(rf/reg-fx
 :xhr
 (fn [{:keys [method] :as opts}]
   (try
     (let [req-fn (xhr-mapper method)]
       (req-fn (str "http://localhost:3000" (:uri opts))
               (-> opts (dissoc :method))))
     (catch js/Error e
       (.log js/console "HTTP Request error" e)))))
