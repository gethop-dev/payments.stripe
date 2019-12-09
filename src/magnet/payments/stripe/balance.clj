;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.balance
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]]
            [magnet.payments.util :as util])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url "/balance"
         :response [:balance :body]}})

(extend-protocol core/Balance
  Stripe
  (get-balance [this]
    (execute this (:get api-definition) {})))
