(ns me.ebbinghaus.payup.server-components.server-main
  (:require
   [me.ebbinghaus.payup.server-components.http-server]
   [mount.core :as mount])
  (:gen-class))

(defn -main [& args]
  (mount/start-with-args {}))