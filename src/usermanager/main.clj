(ns usermanager.main
  (:require [ring.adapter.jetty :as adapter]
            [usermanager.router.core :as router])
  (:gen-class))

(defn echo-handler
  [request]
  {:status 200
   :headers {"Content-Type" "text/plain;charset=utf-8"}
   :body (format "echoing METHOD %s for PATH %s"
                 (:request-method request)
                 (:uri request))})

(defn wrap-router
  [handler]
  (fn [request]
    (router/router handler request)))

(defn -main
  [& _args]
  (adapter/run-jetty (wrap-router echo-handler)
                     {:port 3000 :join? false}))
