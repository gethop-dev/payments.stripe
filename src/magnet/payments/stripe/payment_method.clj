;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.payment-method
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/payment_methods/" %)
         :response [:payment-method :body]}
   :get-all {:method :get
             :url "/payment_methods"
             :response [:payment-methods [:body :data]]}
   :attach {:method :post
            :url #(str "/payment_methods/" % "/attach")
            :response [:payment-method :body]}
   :detach {:method :post
            :url #(str "/payment_methods/" % "/detach")
            :response [:payment-method :body]}})

(extend-protocol core/PaymentMethod
  Stripe
  (get-payment-method [this payment-method-id]
    (execute this (:get api-definition) {:path-params [payment-method-id]}))
  (get-customer-payment-methods [this customer-id payment-method-type opt-args]
    (execute this (:get-all api-definition) {:entity {:customer customer-id
                                                      :type payment-method-type}
                                             :opt-req-args opt-args}))
  (attach-payment-method [this payment-method-id customer-id]
    (execute this (:attach api-definition) {:path-params [payment-method-id]
                                            :entity {:customer customer-id}}))
  (detach-payment-method [this payment-method-id]
    (execute this (:detach api-definition) {:path-params [payment-method-id]})))
