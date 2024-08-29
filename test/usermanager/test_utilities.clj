(ns usermanager.test-utilities
  (:require [usermanager.main :as um]
            [usermanager.system.core :as system]
            [clojure.string :as s]
            [usermanager.model.user-manager :as model]
            [next.jdbc :as jdbc]
            [clojure.java.io :as io])
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
  (system/set-config! server-key {:port (get-free-port!)
                                  :join? false})
  (println (format "\nRunning test with server config: %s\n"
                   (system/get-config server-key)))
  (system/start-server! (um/wrap-router um/echo-handler) server-key)
  (f)
  (system/stop-server! server-key))

(defn with-test-db
  [db-key f]
  (let [dbname (format "test/%s.sqlite3"
                       (-> db-key symbol str
                           (s/replace #"\.|/" "_")))]
    ;; SETUP SYSTEM
    (system/set-config! db-key
                        {:dbtype "sqlite" :dbname dbname})

    ;; SETUP DB
    (println (format "\nCreating DB with config: %s\n"
                     (system/get-config db-key)))
    (system/start-db! model/populate db-key)

    (f) ; RUN TEST

    ;; TEARDOWN DB
    (system/stop-db! db-key)
    (println (format "\nDeleting SQLite test file at: %s\n" dbname))
    (io/delete-file dbname)))

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
