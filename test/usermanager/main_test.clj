(ns usermanager.main-test
  (:require [clojure.test :refer [deftest is testing] :as t]
            [clj-http.client :as http]
            [usermanager.main :as um]
            [usermanager.system.core :refer :all])
  (:import (java.net ServerSocket)))

(defn get-free-port!
  []
  (with-open [socket (ServerSocket. 0)]
    (.getLocalPort socket)))

(defn setup-teardown-server!
  [f]
  (set-config! ::server {:port (get-free-port!)
                         :join? false})
  (println (format "\nRunning test with server config: %s\n"
                   (get-config ::server)))
  (start-server! (um/wrap-router um/echo-handler) ::server)
  (f)
  (stop-server! ::server))

(t/use-fixtures :once setup-teardown-server!)

(deftest a-simple-server-test
  (testing "A simple server test."
    (let [base-uri (.toString (.getURI (get-state ::server)))]
      (is (= {:status 200
              :headers {"Content-Type" "text/plain;charset=utf-8"}
              :body "echoing METHOD :get for PATH /"}
             (-> (http/get base-uri)
                 (select-keys [:status :body :headers])
                 (update :headers (fn [{:strs [Content-Type]}]
                                    {"Content-Type" Content-Type}))))
          "Server echoes back request information in body.")
      (is (= {:status 404
              :body "Not Found."}
             (-> (http/post base-uri {:throw-exceptions false})
                 (select-keys [:status :body])))
          "Server rejects unsupported route pattern."))))
