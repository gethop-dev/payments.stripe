;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe
  (:require [integrant.core :as ig]
            [magnet.payments.stripe.balance]
            [magnet.payments.stripe.balance-transaction]
            [magnet.payments.stripe.card]
            [magnet.payments.stripe.charge]
            [magnet.payments.stripe.checkout]
            [magnet.payments.stripe.core :as core]
            [magnet.payments.stripe.customer]
            [magnet.payments.stripe.event]
            [magnet.payments.stripe.invoice]
            [magnet.payments.stripe.plan]
            [magnet.payments.stripe.product]
            [magnet.payments.stripe.subscription]))

(def ^:const default-timeout
  "Default timeout value for an connection attempt with Stripe API."
  2000)

(def ^:const default-max-retries
  "Default limit of attempts for Stripe request."
  10)

(def ^:const default-initial-delay
  "Initial delay for retries, specified in milliseconds."
  500)

(def ^:const default-max-delay
  "Maximun delay for a connection retry, specified in milliseconds. We
  are using truncated binary exponential backoff, with `max-delay` as
  the ceiling for the retry delay."
  1000)

(def ^:const default-backoff-ms
  [default-initial-delay default-max-delay 2.0])

(defmethod ig/init-key :magnet.payments/stripe [_ {:keys [api-key timeout max-retries backoff-ms]
                                                   :or {timeout default-timeout
                                                        max-retries default-max-retries
                                                        backoff-ms default-backoff-ms}}]
  (core/->Stripe api-key timeout max-retries backoff-ms))
