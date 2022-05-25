;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.ephemeral-key
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:create {:method :post
            :url "/ephemeral_keys"
            :response [:data :body]}})

(extend-protocol core/EphemeralKeys
  Stripe
  (create-ephemeral-key [this opt-args api-version]
    (execute this (:create api-definition) {:opt-req-args opt-args
                                            :api-version api-version})))
