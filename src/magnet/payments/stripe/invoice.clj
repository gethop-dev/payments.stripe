;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.invoice
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]]
            [magnet.payments.util :as util])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/invoices/" %)
         :response [:invoice :body]}
   :get-all {:method :get
             :url "/invoices"
             :response [:invoices [:body :data]]}
   :get-by {:method :get
            :url #(str "/invoices/upcoming?customer=" %)
            :response [:invoice :body]}
   :create {:method :post
            :url "/invoices"
            :response [:invoice :body]}
   :update {:method :post
            :url #(str "/invoices/" %)
            :response [:invoice :body]}})

(extend-protocol core/Invoice
  Stripe
  (create-invoice [this invoice]
    (execute this (:create api-definition) {:entity invoice}))
  (get-invoice [this invoice-id]
    (execute this (:get api-definition) {:path-params [invoice-id]}))
  (get-all-invoices [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args}))
  (get-upcoming-invoice [this customer-id opt-args]
    (execute this (:get-by api-definition) {:path-params [customer-id]
                                            :opt-req-args opt-args}))
  (update-invoice [this invoice-id invoice]
    (execute this (:update api-definition) {:entity invoice
                                            :path-params [invoice-id]})))
