(ns usermanager.http.middleware-test
  (:require
   [clojure.test :as t :refer [deftest is testing]]
   [usermanager.http.middleware :as middleware]))

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
          "Echo middleware composes with message params middleware."))))
