(ns usermanager.main
  (:gen-class)
  (:require
   [usermanager.router.core :as router]
   [usermanager.system.core :as system]
   [usermanager.http.middleware :as middleware]))

(def middleware-stack [middleware/wrap-db])

(defn wrap-router
  ([router]
   (wrap-router router ::system/middleware))
  ([router middleware-key]
   (system/set-config! middleware-key middleware-stack)
   (system/start-middleware-stack! middleware-key)
   (fn [request]
     (let [request-handler (router request)
           app-handler (system/wrap-middleware
                        request-handler
                        middleware-key)]
       (app-handler request)))))

(defn -main
  [& _args]
  (system/start-middleware-stack!)
  (system/start-server! (wrap-router router/router)))

(comment
  (system/start-middleware-stack!)
  (system/start-server! (wrap-router router/router))
  (system/stop-server!)

  system/global-system
  (require 'clojure.reflect)
  (clojure.reflect/reflect (::system/server @system/global-system))
  )
