(ns usermanager.router.core-test
  (:require
   [clojure.test :refer [are deftest testing use-fixtures]]
   [usermanager.router.core :as urc]
   [usermanager.test-utilities :as tu]
   [usermanager.system.core :as system]))

(use-fixtures :each (partial tu/with-test-db ::db))

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

(deftest successive-list-delete-list-route-calls-test
  (let [db (system/get-db ::db)]
    (testing "Whether successive list -> delete -> list behaves as expected."
      (are [route-call response] (= route-call response)

        ;; LIST fresh DB with only one user
        (handle {:request-method :get
                 :uri "/user/list"
                 :application/component {:database db}})
        {:request-method :get,
         :uri "/user/list",
         :application/component
         {:database db},
         :params
         {:users
          [{:addressbook/id 1,
            :addressbook/first_name "Sean",
            :addressbook/last_name "Corfield",
            :addressbook/email "sean@worldsingles.com",
            :addressbook/department_id 4,
            :department/name "Development"}]},
         :application/view "list"}

        ;; Delete the lone user
        (handle {:request-method :delete
                 :uri "/user/delete/42"
                 :params {:id 1} ; We assume setup creates at least one user
                 :application/component {:database db}})
        {:status 303
         :headers
         {"Location" "/user/list"}, :body ""}

        ;; LIST db again to fetch nobody
        (handle {:request-method :get
                 :uri "/user/list"
                 :application/component {:database db}})
        {:request-method :get,
         :uri "/user/list",
         :application/component
         {:database db},
         :params {:users []},
         :application/view "list"}))))
