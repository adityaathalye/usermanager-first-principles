(ns usermanager.http.middleware
  (:require
    [usermanager.http.utils :as resp]
    [usermanager.handlers.user :as handlers]
    [usermanager.system.core :as system]))

(defn wrap-message-param-in-response-header
  [handler]
  (fn [request]
    (let [context (handler request)
          message (get-in context [:params :message])]
      (if message
        (-> context
            (resp/header "UM-Message" message))
        context))))

(defn wrap-echo
  [handler]
  (fn [request]
    (let [context (handler request)]
      (if (resp/response? context)
        context
        (handlers/echo context)))))

(defn wrap-db
  ([handler]
   (wrap-db handler ::system/db))
  ([handler db-key]
   (fn [request]
     (let [request (assoc-in request
                             [:application/component :database]
                             (system/get-db db-key))]
       (handler request)))))

(comment
  ((wrap-message-param-in-response-header
    identity) {:params {:message :hello}})

  ((wrap-message-param-in-response-header
    (fn [request]
      (assoc request :params {:message "foo"})))
    {:request-method :get :uri "/"})

  ((wrap-echo
    (wrap-message-param-in-response-header
     (fn [request]
       (assoc request :params {:message "foo"}))))
   {:request-method :get :uri "/"})

  (let [composed-middleware (apply comp [wrap-message-param-in-response-header wrap-echo])
        handler (fn [request]
                  (assoc request :params {:message "foo"}))]
    ((composed-middleware handler)
     {:request-method :get :uri "/"}))

  )
