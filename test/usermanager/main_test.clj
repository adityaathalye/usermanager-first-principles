(ns usermanager.main-test
  (:require
   [clj-http.client :as http]
   [clojure.test :as t :refer [deftest is testing]]
   [usermanager.system.core :as system]
   [usermanager.http.middleware :as middleware]
   [usermanager.test-utilities :as tu]))

(t/use-fixtures :once
  (partial tu/setup-teardown-server!
           {:server-key ::server
            :middleware-key ::middleware
            :middleware-stack [middleware/wrap-message-param-in-response-header
                               middleware/wrap-echo]}))

(deftest a-simple-server-test
  (testing "A simple server test."
    (let [base-uri (.toString (.getURI (system/get-state ::server)))]
      (is (= {:status 200
              :headers {"Content-Type" "text/plain;charset=utf-8"}
              :body "echoing METHOD :get for PATH /"}
             (-> (http/get base-uri)
                 (select-keys [:status :body :headers])
                 (update :headers (fn [{:strs [Content-Type]}]
                                    {"Content-Type" Content-Type}))))
          "Server echoes back information about request method and uri.")
      (is (= {:status 404
              :body "Not Found."}
             (-> (http/post base-uri {:throw-exceptions false})
                 (select-keys [:status :body])))
          "Server rejects unsupported route pattern."))))
