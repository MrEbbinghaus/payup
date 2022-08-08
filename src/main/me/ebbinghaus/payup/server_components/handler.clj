(ns me.ebbinghaus.payup.server-components.handler
  (:require
   [reitit.core :as r]
   [hiccup.page :refer [html5 include-js include-css]]
   [mount.core :refer [defstate]]
   [me.ebbinghaus.payup.server-components.config :refer [config]]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.dev.pretty :as pretty]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.middleware.defaults :refer [wrap-defaults]]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
   [com.fulcrologic.fulcro.server.api-middleware :refer [handle-api-request]]
   [taoensso.timbre :as log]))

(defn page [{:keys [lang title script-manifest csrf-token]}]
  (html5
   [:html {:lang lang}
    [:head
     [:title title]
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=5"}]

     [:meta {:name "mobile-web-app-capable" :content "yes"}]
     [:meta {:name "apple-mobile-web-app-capable" :content "yes"}]
     [:meta {:name "application-name" :content "PayUp"}]
     [:meta {:name "apple-mobile-web-app-title" :content "PayUp"}]

     [:script (str "var fulcro_network_csrf_token = '" csrf-token "';")]]
    [:body
     [:div#payup "Foo"]
     (for [{:keys [output-name]} script-manifest
           :when output-name]
       (include-js (str "/assets/js/main/" output-name)))]]))

(defn landing [request]
  {:status 200
   :body
   (page {:lang :en
          :title "Welcome to PayUp!"
          :script-manifest (:script-manifest config)
          :csrf-token (:anti-forgery-token request)})})

(defn index
  ([csrf-token script-manifest] (index csrf-token script-manifest nil nil))
  ([csrf-token script-manifest initial-state-script] (index csrf-token script-manifest initial-state-script nil))
  ([csrf-token script-manifest initial-db initial-html]
   (html5
    [:html {:lang "en"}
     [:head
      [:title "PayUp"]
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1, maximum-scale=5"}]
      #_[:link {:rel "icon" :type "image/svg+xml" :href (if (:dev? config) "/assets/icons/favicon_dev.svg" "/assets/icons/favicon.svg")}]

      [:meta {:name "mobile-web-app-capable" :content "yes"}]
      [:meta {:name "apple-mobile-web-app-capable" :content "yes"}]
      [:meta {:name "application-name" :content "PayUp"}]
      [:meta {:name "apple-mobile-web-app-title" :content "PayUp"}]
      ; [:meta {:name "theme-color" :content styles/primary}]
      ; [:meta {:name "msapplication-navbutton-color" :content styles/primary}]
      [:meta {:name "apple-mobile-web-app-status-bar-style" :content "default"}]

      #_(ssr/initial-state->script-tag initial-db)

      #_[:link {:href "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700&display=swap" :rel "stylesheet"}]
      [:script (str "var fulcro_network_csrf_token = '" csrf-token "';")]
      #_[:style (garden/css styles/body styles/splashscreen styles/sizing styles/address)]]
     [:body
      [:div#decide initial-html]
      (for [{:keys [output-name]} script-manifest
            :when output-name]
        (include-js (str "/assets/js/main/" output-name)))]])))




(def routes
  [["/"
    {:name ::landing
     :get {:handler landing}}]
   ["group/:id" ::group]
   ["/assets/*" (ring/create-resource-handler)]
   ["/api" ::api]])

(defn router [config]
  (ring/router routes))

(def ^:private redirect-to-landing
  (fn [req]
    {:status 302
     :headers {"Location" "/"}}))

(defstate handler
  :start
  (ring/ring-handler
   (router config)
   (ring/routes
    (ring/redirect-trailing-slash-handler {:method :strip})
    (ring/create-default-handler
     #_{:not-found redirect-to-landing}))
   {:middleware [[wrap-anti-forgery]]}))