(ns usermanager.main
  "Compare with seancorfield/usermanager-example
  https://github.com/seancorfield/usermanager-example/blob/develop/src/usermanager/main.clj

  This project is a \"from first principles\" variant of Sean's project
  (synced as of commit SHA 2a9cf63).

  It follows up the explanation laid out in Clojuring the web
  application stack: Meditation One:
  https://www.evalapply.org/posts/clojure-web-app-from-scratch/index.html.

  If nothing else, it exists to scratch one's own itch.

  Sean's repo references variants of the same basic \"User Manager\" web
  application. All of these are built with libraries used by Clojure
  professionals in production web apps.

  This variant uses only a small fraction of those dependencies; bare
  essentials like adapters for Jetty, SQLite, and HTML rendering. Also
  some ring middleware that handles HTML form data. Everything else is
  hand-rolled Clojure.

  The resulting app is not fit for production deployment. Expose it to
  the Public Internet only on a throway server instance."
  (:gen-class)
  (:require
   [ring.middleware.params :as params-middleware]
   [ring.middleware.keyword-params :as keyword-params-middleware]
   [usermanager.router.core :as router]
   [usermanager.system.core :as system]
   [usermanager.http.middleware :as middleware]
   [usermanager.model.user-manager :as model]))

(def middleware-stack
  [keyword-params-middleware/wrap-keyword-params
   params-middleware/wrap-params
   middleware/wrap-db
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
  [& [port]]
  (let [server-config (system/get-config ::system/server)
        port (or port
                 (get (System/getenv) "PORT")
                 (get (system/get-config ::system/server)
                      (:port server-config)
                      3000))
        port (if (string? port)
               (Integer/parseInt port)
               port)]
    (println "Setting up DB having dbname: "
             (:dbname (system/get-config ::system/db)))
    (system/start-db! model/populate)
    (println "Starting up Server on port: " port)
    (system/set-config! ::system/server
                        (assoc server-config :port port))
    (system/start-server! (wrap-router router/router))))

(comment
  (let [dev-db-file "dev/usermanager_dev_db.sqlite3"]
    (require 'clojure.java.io)
    (system/stop-server!)
    (system/stop-db!)
    (clojure.java.io/delete-file dev-db-file)
    (system/evict-component! ::system/middleware)
    (system/set-config! ::system/db {:dbtype "sqlite" :dbname dev-db-file})
    (system/start-db! model/populate)
    (system/start-server! (wrap-router router/router)))

  system/global-system
  (require 'clojure.reflect)
  (clojure.reflect/reflect (::system/server @system/global-system))
  )
