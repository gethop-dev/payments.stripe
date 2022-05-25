;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.event
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url "/events"
         :response [:data :body]}})

(extend-protocol core/Events
  Stripe
  (list-events [this event-types opt-args]
    (execute this (:get api-definition) {:entity event-types :opt-req-args opt-args})))
