;; copyright (c) 2019-2023 Sean Corfield, all rights reserved

(ns usermanager.model.user-manager-test
  "These tests use SQLite in-memory."
  (:require [clojure.test :refer [deftest is use-fixtures]]
            [usermanager.model.user-manager :as model]
            [usermanager.system.core :as system]
            [usermanager.test-utilities :as tu]))

(use-fixtures :once (partial tu/with-test-db ::db))

(def db (system/get-db ::db))

(deftest department-test
  (is (= #:department{:id 1 :name "Accounting"}
         (model/get-department-by-id db 1)))
  (is (= 4 (count (model/get-departments db)))))

(deftest user-test
  (is (= 1 (:addressbook/id (model/get-user-by-id db 1))))
  (is (= "Sean" (:addressbook/first_name
                 (model/get-user-by-id db 1))))
  (is (= 4 (:addressbook/department_id
            (model/get-user-by-id db 1))))
  (is (= 1 (count (model/get-users db))))
  (is (= "Development" (:department/name
                        (first
                         (model/get-users db))))))

(deftest save-test
  (is (= "sean@corfield.org"
         (:addressbook/email
          (do
            (model/save-user db {:addressbook/id 1
                                 :addressbook/email "sean@corfield.org"})
            (model/get-user-by-id db 1)))))
  (is (= "seancorfield@hotmail.com"
         (:addressbook/email
          (do
            (model/save-user db {:addressbook/first_name "Sean"
                                 :addressbook/last_name "Corfield"
                                 :addressbook/department_id 4
                                 :addressbook/email "seancorfield@hotmail.com"})
            (model/get-user-by-id db 2))))))
