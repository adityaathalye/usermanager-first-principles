(ns usermanager.test-utilities
  (:require [usermanager.main :as um]
            [usermanager.system.core :as system])
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
