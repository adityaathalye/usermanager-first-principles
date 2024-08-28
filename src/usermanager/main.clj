(ns usermanager.main
  (:require [ring.adapter.jetty :as adapter])
  (:gen-class))

(defn echo-handler
  [request]
  {:status 200
   :headers {"Content-Type" "text/plain;charset=utf-8"}
   :body (format "echoing METHOD %s for PATH %s"
                 (:request-method request)
                 (:uri request))})

(defn -main
  [& _args]
  (adapter/run-jetty echo-handler
                     {:port 3000 :join? false}))
