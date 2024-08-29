(ns usermanager.main
  (:gen-class)
  (:require
   [usermanager.router.core :as router]
   [usermanager.system.core :as system]))

(defn wrap-router
  [router]
  (fn [request]
    (let [handler (router request)]
      (handler request))))

(defn -main
  [& _args]
  (system/start-server! (wrap-router router/router)))

(comment
  (system/start-server! (wrap-router router/router))
  (system/stop-server!)

  (require 'clojure.reflect)
  (clojure.reflect/reflect (::system/server @system/global-system))
  )
