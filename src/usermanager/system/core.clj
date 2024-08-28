(ns usermanager.system.core
  (:require [ring.adapter.jetty :as adapter]))

(defonce global-system
  (atom {::server {:config {:port 3000
                            :join? false}
                   :state nil}}))

(defn- update-system!
  [key-path v]
  (swap! global-system (fn [s] (update-in s key-path (constantly v)))))

(defn set-state!
  [component-key v]
  (update-system! [component-key :state] v))

(defn set-config!
  [component-key v]
  (update-system! [component-key :config] v))

(defn get-config
  [component-key]
  (get-in @global-system [component-key :config]))

(defn get-state
  [component-key]
  (get-in @global-system [component-key :state]))


;; Server start/stop utilities

(defn start-server!
  ([handler]
   (start-server! handler ::server))
  ([handler server-key]
   (when-not (get-state server-key)
     (->> (get-config server-key)
          (adapter/run-jetty handler)
          (set-state! server-key)))
   (get-state server-key)))

(defn stop-server!
  ([]
   (stop-server! ::server))
  ([server-key]
   (when-let [server (get-state server-key)]
     (.stop server)
     (set-state! server-key nil))))

(comment
  global-system
  (set-state! ::server :foo)
  (get-state ::server)
  (set-config! ::server {:port 1337 :join? true})
  (get-config ::server)
  (set-config! ::server {:port 3000 :join? false})
  (set-state! ::server nil)
  )
