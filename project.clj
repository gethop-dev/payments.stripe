(defproject dev.gethop/payments.stripe "0.3.10"
  :description "A Duct library for interacting with the Stripe API"
  :url "http://github.com/gethop-dev/payments.stripe"
  :license {:name "Mozilla Public License 2.0"
            :url "https://www.mozilla.org/en-US/2.0/"}
  :min-lein-version "2.9.8"
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [integrant "0.8.1"]
                 [http-kit/http-kit "2.8.0"]
                 [diehard "0.11.12"]
                 [org.clojure/data.json "2.5.1"]
                 [com.stripe/stripe-java "28.3.1"]]
  :deploy-repositories [["snapshots" {:url "https://clojars.org/repo"
                                      :username :env/CLOJARS_USERNAME
                                      :password :env/CLOJARS_PASSWORD
                                      :sign-releases false}]
                        ["releases"  {:url "https://clojars.org/repo"
                                      :username :env/CLOJARS_USERNAME
                                      :password :env/CLOJARS_PASSWORD
                                      :sign-releases false}]]
  :profiles {:dev [:project/dev :profiles/dev]
             :profiles/dev {}
             :project/dev {:plugins [[jonase/eastwood "1.4.3"]
                                     [dev.weavejester/lein-cljfmt "0.13.0"]]}
             :repl {:repl-options {:init-ns dev.gethop.payments.stripe
                                   :host "0.0.0.0"
                                   :port 4001}}})
