;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.event
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url "/events"
         :response [:data :body]}})

(extend-protocol core/Events
  Stripe
  (list-events [this event-types opt-args]
    (execute this (:get api-definition) {:entity event-types :opt-req-args opt-args})))
