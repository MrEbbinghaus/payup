(ns me.ebbinghaus.payup.client.root
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [com.fulcrologic.fulcro.react.hooks :as hooks]
            [me.ebbinghaus.payup.client.theming.dark-mode :as dark-mode]
            [mui.layout :as layout]
            [mui.utils :as m.utils]
            [taoensso.timbre :as log]))


(defmutation set-theme [{:keys [theme]}]
  (action [{:keys [state]}]
          (swap! state assoc :ui/theme theme)))

(defsc Root [this props]
  {:query []
   :initial-state {}
   :use-hooks? true}
  (hooks/use-lifecycle
   (fn []
     (log/debug "Register dark-mode listener")
     (dark-mode/register-dark-mode-listener #(let [new-theme (if (.-matches %) :dark :light)]
                                               (comp/transact! (comp/any->app this)
                                                               [(set-theme {:theme new-theme})])))))
  (m.utils/css-baseline
   {}
   (layout/container
    {}
    (layout/stack
     {}
     (dom/div "Hello World")))))

