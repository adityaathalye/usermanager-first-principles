(ns usermanager.router.core-test
  (:require [clojure.test :refer [deftest is are testing]]
            [usermanager.router.core :as urc]
            [usermanager.main :as um]))

(defn- echo-response
  [method path]
  {:status 200
   :headers {"Content-Type" "text/plain;charset=utf-8"}
   :body (format "echoing METHOD %s for PATH %s"
                 method path)})

(def not-found-response
  {:status 404
   :headers {}
   :body "Not Found."})

(deftest route-matching-test
  (testing "Only allowed route path patterns match."
    (are [route-call response] (= route-call response)

      (urc/router um/echo-handler {:request-method :get :uri "/"})
      (echo-response :get "/")

      (urc/router um/echo-handler {:request-method :post :uri "/"})
      not-found-response

      (urc/router um/echo-handler {:request-method :post :uri "/does/not/exist"})
      not-found-response

      (urc/router um/echo-handler {:request-method :delete :uri "/user/delete/42"})
      (echo-response :delete "/user/delete/42")

      (urc/router um/echo-handler {:request-method :post :uri "/user/delete/42"})
      not-found-response

      (urc/router um/echo-handler {:request-method :delete :uri "/user/delete/NAN"})
      not-found-response

      (urc/router um/echo-handler {:request-method :get :uri "/user/form/42"})
      (echo-response :get "/user/form/42")

      (urc/router um/echo-handler {:request-method :post :uri "/user/form/42"})
      not-found-response

      (urc/router um/echo-handler {:request-method :form :uri "/user/form/NAN"})
      not-found-response)))
