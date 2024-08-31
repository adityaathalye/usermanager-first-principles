(ns usermanager.router.core-test
  (:require
   [clojure.test :refer [are deftest testing use-fixtures]]
   [usermanager.router.core :as urc]
   [usermanager.test-utilities :as tu]
   [usermanager.system.core :as system]))

(use-fixtures :once (partial tu/with-test-db ::db))

(defn handle
  [request]
  (let [h (urc/router request)]
    (h request)))

(deftest route-matching-test
  (testing "Only allowed route path patterns match."
    (are [route-call response] (= route-call response)

      (handle {:request-method :get :uri "/"})
      {:request-method :get,
       :uri "/",
       :params {:message
                (str "Welcome to the User Manager application demo! "
                     "This is a first principles version of "
                     "searncorfield/usermanager-example.")}}

      (handle {:request-method :post :uri "/"})
      tu/not-found-response

      (handle {:request-method :post :uri "/does/not/exist"})
      tu/not-found-response

      (handle {:request-method :delete
               :uri "/user/delete/42"
               :params {:id 1} ; We assume setup creates at least one user
               :application/component {:database (system/get-db ::db)}})
      {:status 303
       :headers
       {"Location" "/user/list"}, :body ""}

      (handle {:request-method :post :uri "/user/delete/42"})
      tu/not-found-response

      (handle {:request-method :delete :uri "/user/delete/NAN"})
      tu/not-found-response

      (handle {:request-method :get :uri "/user/form/42"})
      (tu/echo-response :get "/user/form/42")

      (handle {:request-method :post :uri "/user/form/42"})
      tu/not-found-response

      (handle {:request-method :form :uri "/user/form/NAN"})
      tu/not-found-response

      (handle {:request-method :get :uri "/reset"})
      {:request-method :get,
       :uri "/reset",
       :params {:message "The change tracker has been reset to 0."}})))
