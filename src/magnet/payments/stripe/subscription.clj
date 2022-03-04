;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.subscription
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/subscriptions/" %)
         :response [:subscription :body]}
   :get-all {:method :get
             :url "/subscriptions"
             :response [:subscriptions [:body :data]]}
   :create {:method :post
            :url "/subscriptions"
            :response [:subscription :body]}
   :update {:method :post
            :url #(str "/subscriptions/" %)
            :response [:subscription :body]}
   :cancel {:method :delete
            :url #(str "/subscriptions/" %)
            :response [:subscription-canceled :body]}})

(extend-protocol core/Subscriptions
  Stripe
  (create-subscription [this subscription]
    (execute this (:create api-definition) {:entity subscription}))
  (get-subscription [this subscription-id]
    (execute this (:get api-definition) {:path-params [subscription-id]}))
  (get-all-subscriptions [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args}))
  (cancel-subscription [this subscription-id opt-args]
    (execute this (:cancel api-definition) {:path-params [subscription-id]
                                            :opt-req-args opt-args}))
  (update-subscription [this subscription-id subscription]
    (execute this (:update api-definition) {:entity subscription
                                            :path-params [subscription-id]})))
