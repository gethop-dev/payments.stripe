;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.payment-intent
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/payment_intents/" %)
         :response [:payment-intent :body]}
   :get-all {:method :get
             :url "/payment_intents"
             :response [:payment-intents [:body :data]]}
   :create {:method :post
            :url "/payment_intents"
            :response [:payment-intent :body]}
   :update {:method :post
            :url #(str "/payment_intents/" %)
            :response [:payment-intent :body]}
   :confirm {:method :post
             :url #(str "/payment_intents/" %)
             :response [:payment-intent-confirmed :body]}
   :capture {:method :post
             :url #(str "/payment_intents/" %)
             :response [:payment-intent-captured :body]}
   :cancel {:method :post
            :url #(str "/payment_intents/" %)
            :response [:payment-intent-canceled :body]}})

(defn update-payment-method-types [payment-intent]
  ;; Update name of the `:payment_method_types` to `"payment_method_types[]"`
  ;; so httpkit can correctly serialize form params in the request
  (if (contains? payment-intent :payment_method_types)
    (let [payment-method-types (:payment_method_types payment-intent)]
      (-> payment-intent
          (dissoc :payment_method_types)
          (assoc "payment_method_types[]" payment-method-types)))
    payment-intent))

(extend-protocol core/PaymentIntents
  Stripe
  (create-payment-intent [this payment-intent]
    (execute this (:create api-definition) {:entity (update-payment-method-types payment-intent)}))
  (get-all-payment-intents [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args}))
  (get-payment-intent [this payment-intent-id]
    (execute this (:get api-definition) {:path-params [payment-intent-id]}))
  (update-payment-intent [this payment-intent-id payment-intent]
    (execute this (:update api-definition) {:entity (update-payment-method-types payment-intent)
                                            :path-params [payment-intent-id]}))
  (confirm-payment-intent [this payment-intent-id opt-args]
    (execute this (:confirm api-definition) {:path-params [payment-intent-id]
                                             :opt-req-args opt-args}))
  (capture-payment-intent [this payment-intent-id opt-args]
    (execute this (:capture api-definition) {:path-params [payment-intent-id]
                                             :opt-req-args opt-args}))
  (cancel-payment-intent [this payment-intent-id opt-args]
    (execute this (:cancel api-definition) {:path-params [payment-intent-id]
                                            :opt-req-args opt-args})))
