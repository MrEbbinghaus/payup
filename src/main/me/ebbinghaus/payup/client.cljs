(ns me.ebbinghaus.payup.client
  (:require
   [com.fulcrologic.fulcro.algorithms.merge :as mrg]
   [com.fulcrologic.fulcro.algorithms.server-render :as ssr]
   [com.fulcrologic.fulcro.algorithms.timbre-support :refer [console-appender prefix-output-fn]]
   [com.fulcrologic.fulcro.application :as app]
   [com.fulcrologic.fulcro.components :as comp]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.raw.components :as rc]
   [com.fulcrologic.fulcro.react.error-boundaries :as eb]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [me.ebbinghaus.payup.client.root :as root]
   [me.ebbinghaus.payup.application :refer [Application]]
   [taoensso.timbre :as log]))

(defn ^:export refresh []
  (log/info "Hot code Remount")
  #_(comp/refresh-dynamic-queries! Application)
  (app/mount! Application root/Root "payup" {:initialize-state? false}))

(defn ^:export init []
  (let [db (ssr/get-SSR-initial-state)]
    (log/merge-config!
     {:output-fn prefix-output-fn
      :appenders {:console (console-appender)}})
    (log/info "Application starting.")
    (app/set-root! Application root/Root {:initialize-state? true})
    (swap! (::app/state-atom Application) merge db)

    (app/mount! Application root/Root "payup"
                {:initialize-state? false
                 :hydrate false})))