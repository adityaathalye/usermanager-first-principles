(ns usermanager.handlers.user)

(defn echo
  [request]
  {:status 200
   :headers {"Content-Type" "text/plain;charset=utf-8"}
   :body (format "echoing METHOD %s for PATH %s"
                 (:request-method request)
                 (:uri request))})

(defn not-found
  [_request]
  {:status 404
   :headers {}
   :body "Not Found."})
