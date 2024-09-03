;; copyright (c) 2019-2023 Sean Corfield, all rights reserved

(ns usermanager.handlers.user
  "Compare with \"controllers\" from seancorfield/usermanager-example

Ref: https://github.com/seancorfield/usermanager-example/blob/develop/src/usermanager/controllers/user.clj
"
  (:require [usermanager.model.user-manager :as model]
            [usermanager.http.utils :as resp]
            [usermanager.layouts.core :as ulc]
            [hiccup.page :as hp]))

(defn echo
  [request]
  (-> (format "echoing METHOD %s for PATH %s"
              (:request-method request)
              (:uri request))
      (resp/response (:headers request))
      (resp/content-type "text/plain;charset=utf-8")))

(defn not-found
  [_request]
  (resp/not-found "Not Found."))

(def ^:private changes
  "Count the number of changes (since the last reload)."
  (atom 0))

(defn render-page
  "Each handler function here adds :application/view to the request
  data to indicate which view file they want displayed. This allows
  us to put the rendering logic in one place instead of repeating it
  for every handler."
  [req]
  (let [req (assoc-in req [:params :changes] @changes)]
    (-> req
        (ulc/hydrate-view)
        (hp/html5)
        (str)
        (resp/response)
        (resp/content-type "text/html"))))

(comment
  (render-page {:request-method :get :uri "/"})
  )

(defn reset-changes
  [req]
  (reset! changes 0)
  (assoc-in req [:params :message] (format "The change tracker has been reset to %s."
                                           @changes)))

(defn default
  [req]
  (assoc-in req [:params :message]
            (str "Welcome to the User Manager application demo! "
                 "This is a first principles version of searncorfield/usermanager-example.")))

(defn delete-by-id
  "Compojure has already coerced the :id parameter to an int."
  [req]
  (swap! changes inc)
  (model/delete-user-by-id (-> req :application/component :database)
                           (get-in req [:params :id]))
  (resp/redirect "/user/list"))

(defn edit
  "Display the add/edit form.

  If the :id parameter is present, Compojure will have coerced it to an
  int and we can use it to populate the edit form by loading that user's
  data from the addressbook."
  [req]
  (let [db   (-> req :application/component :database)
        user (when-let [id (get-in req [:params :id])]
               (model/get-user-by-id db id))]
    (-> req
        (update :params assoc
                :user user
                :departments (model/get-departments db))
        (assoc :application/view "form"))))

(defn get-users
  "Render the list view with all the users in the addressbook."
  [req]
  (let [users (model/get-users (-> req :application/component :database))]
    (-> req
        (assoc-in [:params :users] users)
        (assoc :application/view "list"))))

(defn save
  "This works for saving new users as well as updating existing users, by
  delegatin to the model, and either passing nil for :addressbook/id or
  the numeric value that was passed to the edit form."
  [req]
  (swap! changes inc)
  (-> req
      :params
      ;; get just the form fields we care about:
      (select-keys [:id :first_name :last_name :email :department_id])
      ;; convert form fields to numeric:
      (update :id            #(some-> % not-empty Long/parseLong))
      (update :department_id #(some-> % not-empty Long/parseLong))
      ;; qualify their names for domain model:
      (->> (reduce-kv (fn [m k v] (assoc! m (keyword "addressbook" (name k)) v))
                      (transient {}))
           (persistent!)
           (model/save-user (-> req :application/component :database))))
  (resp/redirect "/user/list"))
