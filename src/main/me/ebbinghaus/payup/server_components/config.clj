(ns me.ebbinghaus.payup.server-components.config
  (:require
   [mount.core :as mount]
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [clojure.pprint :as pprint]
   [taoensso.timbre :as log]))

(defn prettify-data [data]
  (if (string? data)
    data
    (with-out-str (pprint/pprint data))))

(defn pretty-print-middleware [{:keys [level] :as data}]
  (cond-> data
    (#{:debug} level) (update :vargs (partial mapv prettify-data))))

(defn configure-logging! [config]
  (let [{:keys [taoensso.timbre/logging-config]} config]
    (log/info "Configuring Timbre with " logging-config)
    (-> logging-config
        (assoc :middleware [pretty-print-middleware])
        log/merge-config!)))

(mount/defstate config
  :start
  {:org.httpkit.server/config {:port 3000}
   :script-manifest (edn/read-string (slurp (io/resource "public/js/main/manifest.edn")))})
