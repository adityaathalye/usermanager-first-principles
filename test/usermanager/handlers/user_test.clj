(ns usermanager.handlers.user-test
  (:require [clj-http.client :as http]
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

(deftest default-route-message-test
  (testing "Testing that the default route injects a message in params."
    (let [base-uri (.toString (.getURI (system/get-state ::server)))]
      (is (= {:status 200
              :headers {"Content-Type" "text/plain;charset=utf-8"
                        "UM-Message"
                        (str "Welcome to the User Manager application demo!"
                             " "
                             "This is a first principles version of searncorfield/usermanager-example.")}
              :body "echoing METHOD :get for PATH /"}
             (-> (http/get base-uri)
                 (select-keys [:status :body :headers])
                 (update :headers (fn [{:strs [Content-Type UM-Message]}]
                                    {"Content-Type" Content-Type
                                     "UM-Message" UM-Message}))))))))

(deftest reset-route-message-test
  (testing "Testing that the reset route injects a message in params."
    (let [base-uri (.toString (.getURI (system/get-state ::server)))]
      (is (= {:status 200
              :headers {"Content-Type" "text/plain;charset=utf-8"
                        "UM-Message" "The change tracker has been reset to 0."}
              :body "echoing METHOD :get for PATH /reset"}
             (-> (http/get (str base-uri "/reset"))
                 (select-keys [:status :body :headers])
                 (update :headers (fn [{:strs [Content-Type UM-Message]}]
                                    {"Content-Type" Content-Type
                                     "UM-Message" UM-Message}))))))))
