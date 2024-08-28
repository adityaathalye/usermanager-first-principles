(ns usermanager.main
  (:require [usermanager.system.core :as system]
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
  (system/start-server! (wrap-router echo-handler)))

(comment
  (system/start-server! (wrap-router #'echo-handler))
  (system/stop-server!)

  (require 'clojure.reflect)
  (clojure.reflect/reflect (::system/server @system/global-system))
  )
