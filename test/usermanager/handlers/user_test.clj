(ns usermanager.handlers.user-test
  (:require [clj-http.client :as http]
            [clojure.test :as t :refer [deftest is testing]]
            [usermanager.system.core :as system]
            [usermanager.test-utilities :as tu]))

(t/use-fixtures :once (partial tu/setup-teardown-server! ::server))

(deftest default-route-message-test
  (testing "Testing that the default route injects a message in params."
    (let [base-uri (.toString (.getURI (system/get-state ::server)))]
      (is (= {:status 200
              :headers {"Content-Type" "text/plain;charset=utf-8"
                        "UM-Message" "Welcome to the User Manager application demo! This is a first principles version of searncorfield/usermanager-example."}
              :body "echoing METHOD :get for PATH /"}
             (-> (http/get base-uri)
                 (select-keys [:status :body :headers])
                 (update :headers (fn [{:strs [Content-Type UM-Message]}]
                                    {"Content-Type" Content-Type
                                     "UM-Message" UM-Message}))))))))
