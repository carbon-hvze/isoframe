(ns isoframe.xhr
  (:require [ajax.core :as ajax]
            [isoframe.cookies :as cookies]
            [re-frame.core :as rf]))

(defn add-authorization [opts]
  (let [token (cookies/get-cookie :auth)]
    (if token
      (assoc opts :headers {"Authorization" (str "Bearer " token)})
      opts)))

(defn request [{:keys [uri method format] :as opts}]
  (try
    (let [nm (.. js/window -location -href)]
      (method (str (when (str/includes? nm "localhost") "http://localhost:3000") (:uri opts))
              (-> opts
                  (dissoc :method)
                  add-authorization)))
    (catch js/Error e
      (.log js/console "HTTP Request error" e))))

(rf/reg-fx ::POST   (fn [opts] (request (merge opts {:method ajax/POST}))))
(rf/reg-fx ::PUT    (fn [opts] (request (merge opts {:method ajax/PUT}))))
(rf/reg-fx ::DELETE (fn [opts] (request (merge opts {:method ajax/DELETE}))))
(rf/reg-fx ::PATCH  (fn [opts] (request (merge opts {:method ajax/PATCH}))))
(rf/reg-fx ::GET    (fn [opts] (request (merge opts {:method ajax/GET}))))
