(ns usermanager.system.core
  (:require
   [ring.adapter.jetty :as adapter]
   [next.jdbc :as jdbc]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Abstract system utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defonce global-system
  (atom {::server {:config {:port 3000
                            :join? false}
                   :state nil}
         ::db {:config {:dbtype "sqlite" :dbname "dev/usermanager_dev_db.sqlite3"}
               :state nil}
         ::middleware {:config {:stack []}
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

(defn evict-component!
  [component-key]
  (swap! global-system dissoc component-key))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Server start/stop utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Database start/stop utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-db
  ([]
   (get-db ::db))
  ([db-key]
   (fn []
     (get-state db-key))))

(defn start-db!
  ([migrator] (start-db! migrator ::db))
  ([migrator db-key]
   (when-not (get-state db-key)
     (let [datasource (jdbc/get-datasource (get-config db-key))]
       (set-state! db-key datasource)
       (migrator (get-db db-key))
       datasource))))

(defn stop-db!
  ([]
   (stop-db! ::db))
  ([db-key]
   (set-state! db-key nil)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Middleware configuration utilities
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn wrap-middleware
  ([handler]
   (wrap-middleware handler ::middleware))
  ([handler middleware-key]
   (let [composed-middleware (get-state middleware-key)]
     (composed-middleware handler))))

(defn start-middleware-stack!
  ([]
   (start-middleware-stack! ::middleware))
  ([middleware-key]
   (when-not (get-state middleware-key)
     (let [middleware-stack (:stack (get-config middleware-key))]
       (set-state! middleware-key
                   (apply comp (reverse middleware-stack)))))))

(comment

  (do
    (def dummy-handler identity)

    ;; Middleware must be applied in correct order of dependency
    (defn wrap-inc [h] (fn [req] (inc (h req))))
    (defn wrap-str [h] (fn [req] (str (h req))))
    (defn wrap-vec [h] (fn [req] (vec (h req))))

    (let [composed-middleware
          (apply comp
                 (reverse [wrap-inc    ; 0th middleware wrapped first
                           wrap-str    ; 1st middleware wrapped next
                           wrap-vec])) ; 2nd middleware wrapped last

          wrapped-handler
          (composed-middleware dummy-handler)]
      (wrapped-handler 42)))

  global-system

  (set-state! ::server :foo)
  (get-state ::server)
  (set-config! ::server {:port 1337 :join? true})
  (get-config ::server)
  (set-config! ::server {:port 3000 :join? false})
  (set-state! ::server nil)

  (start-db! (constantly :foo))

  (usermanager.model.user-manager/populate (get-db))

  (require 'clojure.reflect)
  (clojure.reflect/reflect (get-db))
  (stop-db!)

  )
