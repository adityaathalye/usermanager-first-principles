(ns usermanager.test-utilities
  (:require [usermanager.main :as um]
            [usermanager.system.core :as system]
            [clojure.string :as s]
            [usermanager.model.user-manager :as model]
            [clojure.java.io :as io]
            [usermanager.router.core :as router])
  (:import (java.net ServerSocket)))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Configuration utils
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-free-port!
  []
  (with-open [socket (ServerSocket. 0)]
    (.getLocalPort socket)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Test fixture utils
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn setup-teardown-server!
  [server-key f]

  (println "Setting up component:" server-key)
  (system/set-config!
   server-key {:port (get-free-port!) :join? false})
  (system/start-server!
   (um/wrap-router router/router) server-key)

  (println "Running test with config:" (system/get-config server-key))
  (f)

  (system/stop-server! server-key)
  (system/evict-component! server-key)
  (println "Stopped and evicted component:" server-key))

(defn with-test-db
  [db-key f]
  (let [dbname (format "test/%s.sqlite3"
                       (-> db-key symbol str
                           (s/replace #"\.|/" "_")))]
    (println "Setting up component:" db-key)
    (system/set-config! db-key {:dbtype "sqlite" :dbname dbname})
    (system/start-db! model/populate db-key)

    (println "Running test with config:" (system/get-config db-key))
    (f)

    (system/stop-db! db-key)
    (system/evict-component! db-key)
    (println "Stopped and evicted component:" db-key)
    (io/delete-file dbname)
    (println "Deleted SQLite test DB:" dbname)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HTTP utils
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn echo-response
  [method path]
  {:status 200
   :headers {"Content-Type" "text/plain;charset=utf-8"}
   :body (format "echoing METHOD %s for PATH %s"
                 method path)})

(def not-found-response
  {:status 404
   :headers {}
   :body "Not Found."})
