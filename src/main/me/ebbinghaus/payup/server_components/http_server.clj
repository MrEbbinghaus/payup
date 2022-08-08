(ns me.ebbinghaus.payup.server-components.http-server
  (:require
   [mount.core :as mount]
   [org.httpkit.server :as http-kit]
   [me.ebbinghaus.payup.server-components.config :refer [config]]
   [me.ebbinghaus.payup.server-components.handler :as handler]
   [taoensso.timbre :as log]
   [clojure.pprint :as pprint]))

(mount/defstate http-server
  :start
  (let [cfg (::http-kit/config config)]
    (log/info "Starting HTTP Server with config " (with-out-str (pprint/pprint cfg)))
    (http-kit/run-server handler/handler cfg))
  :stop (http-server))