(ns usermanager.main
  (:gen-class)
  (:require
   [usermanager.router.core :as router]
   [usermanager.system.core :as system]
   [usermanager.http.middleware :as middleware]
   [usermanager.model.user-manager :as model]))

(def middleware-stack [middleware/wrap-db
                       middleware/wrap-render-page])

(defn wrap-router
  ([router]
   (wrap-router router ::system/middleware))
  ([router middleware-key]
   (system/set-config! middleware-key {:stack middleware-stack})
   (system/start-middleware-stack! middleware-key)
   (fn [request]
     (let [request-handler (router request)
           app-handler (system/wrap-middleware
                        request-handler
                        middleware-key)]
       (app-handler request)))))

(defn -main
  [& _args]
  (system/start-db! model/populate)
  (system/start-middleware-stack!)
  (system/start-server! (wrap-router router/router)))

(comment
  (do
    (system/stop-server!)
    (system/stop-db!)
    (system/evict-component! ::system/middleware)
    (system/start-db! model/populate)
    (system/start-server! (wrap-router router/router)))

  system/global-system
  (require 'clojure.reflect)
  (clojure.reflect/reflect (::system/server @system/global-system))
  )
