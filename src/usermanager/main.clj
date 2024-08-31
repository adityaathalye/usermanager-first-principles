(ns usermanager.main
  (:gen-class)
  (:require
   [usermanager.router.core :as router]
   [usermanager.system.core :as system]))

(defn wrap-router
  [router & middleware-key]
  (apply system/start-middleware-stack! middleware-key)
  (fn [request]
    (let [request-handler (router request)
          app-handler (apply system/wrap-middleware
                             request-handler
                             middleware-key)]
      (app-handler request))))

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
