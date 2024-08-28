(ns usermanager.router.core
  (:require [clojure.string :as s]))

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
  (fn [_handler {:keys [request-method uri] :as _request}]
    [request-method (s/replace uri #"\d+" ":id")]))

(defmethod router :default
  [_handler _request]
  {:status 404
   :headers {}
   :body "Not Found."})

(defmethod router [:get "/"]
  [handler request]
  (handler request))

(defmethod router [:delete "/user/delete/:id"]
  [handler request]
  (handler request))

(defmethod router [:get "/user/form"]
  [handler request]
  (handler request))

(defmethod router [:get "/user/form/:id"]
  [handler request]
  (handler request))

(defmethod router [:get "/user/list"]
  [handler request]
  (handler request))

(defmethod router [:post "/user/save"]
  [handler request]
  (handler request))

(defmethod router [:get "/reset"]
  [handler request]
  (handler request))

(comment
  (require 'usermanager.main)

  (router usermanager.main/echo-handler
          {:request-method :get :uri "/"})

  (router usermanager.main/echo-handler
          {:request-method :delete :uri "/user/delete/42"})

  (router usermanager.main/echo-handler
          {:request-method :post :uri "/"})
  )
