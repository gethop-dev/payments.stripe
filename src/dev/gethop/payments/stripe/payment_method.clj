;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.payment-method
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const api-definition
  {::get {:method :get
          :url #(str "/payment_methods/" %)
          :response [:payment-method :body]}
   ::get-all {:method :get
              :url "/payment_methods"
              :response [:payment-methods [:body :data]]}
   ::attach {:method :post
             :url #(str "/payment_methods/" % "/attach")
             :response [:payment-method :body]}
   ::detach {:method :post
             :url #(str "/payment_methods/" % "/detach")
             :response [:payment-method :body]}
   ::customer-single {:method :get
                      :url (fn [customer-id payment-method-id]
                             (str "/customers/" customer-id "/payment_methods/" payment-method-id))
                      :response [:payment-method :body]}
   ::customer-all {:method :get
                   :url (fn [id] (str "/customers/" id "/payment_methods"))
                   :response [:payment-methods [:body :data]]}
   ::create {:method :post
             :url "/payment_methods"
             :response  [:payment-method :body]}
   ::update {:method :post
             :url #(str "/payment_methods/" %)
             :response  [:payment-method :body]}})

(extend-protocol core/PaymentMethod
  Stripe
  (create-payment-method [this payment-method]
    (execute this (::create api-definition) {:entity payment-method}))
  (update-payment-method [this payment-method-id payment-method]
    (execute this (::update api-definition) {:path-params [payment-method-id]
                                             :entity payment-method}))
  (get-customer-payment-method [this customer-id payment-method-id]
    (execute this (::customer-single api-definition) {:path-params [customer-id payment-method-id]}))
  (get-payment-method [this payment-method-id]
    (execute this (::get api-definition) {:path-params [payment-method-id]}))

  (get-customer-payment-methods [this customer-id payment-method-type opt-args]
    (execute this (::customer-all api-definition) {:path-params [customer-id]
                                                   :opt-req-args (cond-> opt-args
                                                                   payment-method-type
                                                                   (assoc :type payment-method-type))}))
  (get-payment-methods [this opt-args]
    (execute this (::get-all api-definition) {:opt-req-args opt-args}))
  (attach-payment-method [this payment-method-id customer-id]
    (execute this (::attach api-definition) {:path-params [payment-method-id]
                                             :entity {:customer customer-id}}))
  (detach-payment-method [this payment-method-id]
    (execute this (::detach api-definition) {:path-params [payment-method-id]})))
