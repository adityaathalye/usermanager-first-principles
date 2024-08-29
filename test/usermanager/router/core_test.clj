(ns usermanager.router.core-test
  (:require
   [clojure.test :refer [are deftest testing]]
   [usermanager.main :as um]
   [usermanager.router.core :as urc]
   [usermanager.test-utilities :as tu]))

(deftest route-matching-test
  (testing "Only allowed route path patterns match."
    (are [route-call response] (= route-call response)

      (urc/router um/echo-handler {:request-method :get :uri "/"})
      (tu/echo-response :get "/")

      (urc/router um/echo-handler {:request-method :post :uri "/"})
      tu/not-found-response

      (urc/router um/echo-handler {:request-method :post :uri "/does/not/exist"})
      tu/not-found-response

      (urc/router um/echo-handler {:request-method :delete :uri "/user/delete/42"})
      (tu/echo-response :delete "/user/delete/42")

      (urc/router um/echo-handler {:request-method :post :uri "/user/delete/42"})
      tu/not-found-response

      (urc/router um/echo-handler {:request-method :delete :uri "/user/delete/NAN"})
      tu/not-found-response

      (urc/router um/echo-handler {:request-method :get :uri "/user/form/42"})
      (tu/echo-response :get "/user/form/42")

      (urc/router um/echo-handler {:request-method :post :uri "/user/form/42"})
      tu/not-found-response

      (urc/router um/echo-handler {:request-method :form :uri "/user/form/NAN"})
      tu/not-found-response)))
