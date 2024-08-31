(ns usermanager.http.middleware-test
  (:require
   [clojure.test :as t :refer [deftest is testing use-fixtures]]
   [usermanager.http.middleware :as middleware]
   [usermanager.test-utilities :as tu]
   [usermanager.system.core :as system]))

(use-fixtures :once (partial tu/with-test-db ::db))

(deftest middleware-test
  (testing "Middleware functionality"
    (let [message (str (random-uuid))]
      (is (= (let [handler (middleware/wrap-message-param-in-response-header
                            identity)]
               (handler
                {:params {:message message}}))
             {:params {:message message}
              :headers {"UM-Message" message}})
          "Wrap message params middleware injects header in request.")
      (is (= (let [handler (middleware/wrap-echo identity)]
               (handler
                {:request-method :get :uri "/"}))
             {:status 200,
              :headers {"Content-Type" "text/plain;charset=utf-8"},
              :body "echoing METHOD :get for PATH /"})
          "Echo middleware produces an OK response with request information in body.")
      (is (= (let [handler (-> (fn [request] (assoc request :params {:message message}))
                               middleware/wrap-message-param-in-response-header
                               middleware/wrap-echo)]
               (handler
                {:request-method :get :uri "/"}))
             {:status 200,
              :headers
              {"UM-Message" message, "Content-Type" "text/plain;charset=utf-8"},
              :body "echoing METHOD :get for PATH /"})
          "Echo middleware composes with message params middleware.")
      (is (= (let [handler (middleware/wrap-db identity ::db)
                   response (handler {:request-method :get :uri "/"})
                   db (get-in response [:application/component :database])]
               (db))
             (system/get-state ::db))
          "Wrap db injects the database into the request's component context.")
      (is (= (let [uri-prefix "/some/prefix/path/"
                   test-id 1337
                   uri (str uri-prefix test-id)
                   handler (middleware/wrap-route-id-params identity uri-prefix)]
               (handler {:uri uri}))
             {:uri "/some/prefix/path/1337" :params {:id 1337}})))))
