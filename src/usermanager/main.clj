(ns usermanager.main
  (:gen-class)
  (:require
   [usermanager.router.core :as router]
   [usermanager.system.core :as system]))

(defn wrap-router
  [router]
  (system/start-middleware-stack!)
  (fn [request]
    (let [routes->handler (router request)
          handler (system/wrap-middleware routes->handler)]
      (handler request))))

(defn -main
  [& _args]
  (system/start-server! (wrap-router router/router)))

(comment
  (system/start-server! (wrap-router router/router))
  (system/stop-server!)

  system/global-system
  (require 'clojure.reflect)
  (clojure.reflect/reflect (::system/server @system/global-system))
  )
