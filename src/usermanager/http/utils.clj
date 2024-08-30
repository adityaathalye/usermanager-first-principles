(ns usermanager.http.utils)

(defn status
  "Set or override status of response."
  [response status-code]
  (assoc response :status status-code))

(defn status?
  [{:keys [status] :as _response}]
  (and status (< 99 status 600)))

(defn header
  [response header-name header-value]
  (assoc-in response [:headers header-name] header-value))

(defn content-type
  [response content-type]
  (header response "Content-Type" content-type))

(defn response
  "Skeleton response with status 200 OK."
  ([body]
   (response body nil))
  ([body & headers]
   {:status 200
    :headers (into {} headers)
    :body body}))

(defn not-found
  [body]
  {:status 404
   :headers {}
   :body body})

(defn redirect
  [uri]
  {:status 303
   :headers {"Location" uri}
   :body ""})
