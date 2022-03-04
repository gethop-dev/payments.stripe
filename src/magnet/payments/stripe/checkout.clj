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
