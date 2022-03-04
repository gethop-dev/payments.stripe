;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.checkout
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:create {:method :post
            :url "/checkout/sessions"
            :response [:session :body]}})

(extend-protocol core/Checkout
  Stripe
  (create-checkout-session [this checkout-session]
    (execute this (:create api-definition) {:entity checkout-session})))
