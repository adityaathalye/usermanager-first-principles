(ns usermanager.router.core
  (:require [clojure.string :as s]
            [usermanager.handlers.user :as handlers]
            [usermanager.http.middleware :as middleware]))

;; Routes as of this commit from seancorfield/usermanager-example:
;; https://github.com/seancorfield/usermanager-example/blob/2a9cf635cf255bf223486bc9e907a02435c7201c/src/usermanager/main.clj#L113
;;
;; (GET  "/"                        []              (wrap #'user-ctl/default))
;; ;; horrible: application should POST to this URL!
;; (GET  "/user/delete/:id{[0-9]+}" [id :<< as-int] (wrap #'user-ctl/delete-by-id))
;; ;; add a new user:
;; (GET  "/user/form"               []              (wrap #'user-ctl/edit))
;; ;; edit an existing user:
;; (GET  "/user/form/:id{[0-9]+}"   [id :<< as-int] (wrap #'user-ctl/edit))
;; (GET  "/user/list"               []              (wrap #'user-ctl/get-users))
;; (POST "/user/save"               []              (wrap #'user-ctl/save))
;; ;; this just resets the change tracker but really should be a POST :)
;; (GET  "/reset"                   []              (wrap #'user-ctl/reset-changes))
;; (route/resources "/")
;; (route/not-found "Not Found")


#_(def router nil) ; evaluate to reset defmulti after modifying dispatch function
(defmulti router
  (fn [{:keys [request-method uri] :as _request}]
    [request-method (s/replace uri #"\d+" ":id")]))

(defmethod router :default
  [_request]
  handlers/not-found)

(defmethod router [:get "/"]
  [_]
  handlers/default)

;; NOTE: This is an ugly hack. GET is not
;; meant to issue delete requests. If this
;; annoys you, please review the reading
;; guide in the README.
(defmethod router [:get "/user/delete/:id"]
  [_]
  (middleware/wrap-route-id-params
   handlers/delete-by-id
   "/user/delete/"))

(defmethod router [:get "/user/form"]
  [_]
  handlers/edit)

(defmethod router [:get "/user/form/:id"]
  [_]
  (middleware/wrap-route-id-params
   handlers/edit
   "/user/form/"))

(defmethod router [:get "/user/list"]
  [_]
  handlers/get-users)

(defmethod router [:post "/user/save"]
  [_]
  handlers/save)

(defmethod router [:get "/reset"]
  [_]
  handlers/reset-changes)

(comment

  (defn handle
    [request]
    (let [handler (router request)]
      (handler request)))

  (handle {:request-method :get :uri "/"})

  (do
    #_(deref usermanager.system.core/global-system)

    (usermanager.system.core/start-db!
     usermanager.model.user-manager/populate)

    (handle {:request-method :delete
             :uri "/user/delete/42"
             :application/component {:database (usermanager.system.core/get-db)}}))

  (handle {:request-method :post :uri "/"})
  )
