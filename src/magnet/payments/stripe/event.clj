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
