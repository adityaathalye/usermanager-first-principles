(ns usermanager.router.core-test
  (:require
   [clojure.test :refer [are deftest testing]]
   [usermanager.router.core :as urc]
   [usermanager.test-utilities :as tu]))

(defn handle
  [request]
  (let [h (urc/router request)]
    (h request)))

(deftest route-matching-test
  (testing "Only allowed route path patterns match."
    (are [route-call response] (= route-call response)

      (handle {:request-method :get :uri "/"})
      {:status 200,
       :headers
       {"Content-Type" "text/plain;charset=utf-8",
        "UM-Message"
        "Welcome to the User Manager application demo! This is a first principles version of searncorfield/usermanager-example."},
       :body "echoing METHOD :get for PATH /"}

      (handle {:request-method :post :uri "/"})
      tu/not-found-response

      (handle {:request-method :post :uri "/does/not/exist"})
      tu/not-found-response

      (handle {:request-method :delete :uri "/user/delete/42"})
      (tu/echo-response :delete "/user/delete/42")

      (handle {:request-method :post :uri "/user/delete/42"})
      tu/not-found-response

      (handle {:request-method :delete :uri "/user/delete/NAN"})
      tu/not-found-response

      (handle {:request-method :get :uri "/user/form/42"})
      (tu/echo-response :get "/user/form/42")

      (handle {:request-method :post :uri "/user/form/42"})
      tu/not-found-response

      (handle {:request-method :form :uri "/user/form/NAN"})
      tu/not-found-response)))
