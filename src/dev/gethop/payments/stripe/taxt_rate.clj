;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.taxt-rate
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:private url-path "/tax_rates")
(def ^:private response-path [:tax-rates :body])

(defn- url-fn [id] (str url-path "/" id))

(def api-definition
  {::create {:method :post
             :url url-path
             :response response-path}
   ::update {:method :post
             :url url-fn
             :response response-path}
   ::get {:method :get
          :url url-fn
          :response response-path}
   ::get-all {:method :get
              :url url-path
              :response [:tax-rates [:body :data]]}})

(extend-protocol core/TaxRate
  Stripe
  (create-tax-rate [this tax-rate]
    (execute this (::create api-definition) {:entity tax-rate}))
  (update-tax-rate [this tax-rate-id tax-rate]
    (execute this (::update api-definition) {:path-params [tax-rate-id]
                                             :entity tax-rate}))
  (get-all-tax-rates [this opt-args]
    (execute this (::get-all api-definition) {:opt-req-args opt-args}))
  (get-tax-rate [this tax-rate-id]
    (execute this (::get api-definition) {:path-params [tax-rate-id]})))
