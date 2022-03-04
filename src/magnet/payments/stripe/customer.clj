;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.customer
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/customers/" %)
         :response [:customer :body]}
   :get-all {:method :get
             :url "/customers"
             :response [:customers [:body :data]]}
   :create {:method :post
            :url "/customers"
            :response [:customer :body]}
   :update {:method :post
            :url #(str "/customers/" %)
            :response [:customer :body]}
   :delete {:method :delete
            :url #(str "/customers/" %)
            :response [:customer-deleted :body]}
   :get-tax-id {:method :get
                :url #(str "/customers/" %1 "/tax_ids/" %2)
                :response [:tax-id :body]}
   :create-tax-id {:method :post
                   :url #(str "/customers/" % "/tax_ids")
                   :response [:tax-id :body]}
   :delete-tax-id {:method :delete
                   :url #(str "/customers/" %1 "/tax_ids/" %2)
                   :response [:deleted-tax-id :body]}})

(extend-protocol core/Customers
  Stripe
  (create-customer [this customer]
    (execute this (:create api-definition) {:entity customer}))
  (get-customer [this customer-id]
    (execute this (:get api-definition) {:path-params [customer-id]}))
  (get-all-customers [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args}))
  (delete-customer [this customer-id]
    (execute this (:delete api-definition) {:path-params [customer-id]}))
  (update-customer [this customer-id customer]
    (execute this (:update api-definition) {:path-params [customer-id]
                                            :entity customer}))
  (get-customer-tax-id [this customer-id tax-id]
    (execute this (:get-tax-id api-definition) {:path-params [customer-id tax-id]}))
  (create-customer-tax-id [this customer-id tax-id]
    (execute this (:create-tax-id api-definition) {:path-params [customer-id]
                                                   :entity tax-id}))
  (delete-customer-tax-id [this customer-id tax-id]
    (execute this (:delete-tax-id api-definition) {:path-params [customer-id tax-id]})))
