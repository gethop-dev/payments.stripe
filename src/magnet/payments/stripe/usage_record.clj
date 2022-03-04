(ns magnet.payments.stripe.usage-record
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:create {:method :post
            :url #(str "/subscription_items/" % "/usage_records")
            :response [:usage-record :body]}
   :get-all {:method :get
             :url #(str "/subscription_items/" % "/usage_record_summaries")
             :response [:summaries [:body :data]]}})

(extend-protocol core/UsageRecord
  Stripe
  (create-usage-record [this subscription-item-id usage-record]
    (execute this (:create api-definition) {:path-params [subscription-item-id]
                                            :entity usage-record}))
  (get-usage-record-summaries [this subscription-item-id opt-args]
    (execute this (:get-all api-definition) {:path-params [subscription-item-id]
                                             :opt-req-args opt-args})))
