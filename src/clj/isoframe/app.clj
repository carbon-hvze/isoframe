(ns isoframe.app)

(defn hello-world [ctx]
  {:status 200
   :body "HELLO, WORLD!"})

(def routes
  `[[["/" {:get hello-world}]]])
