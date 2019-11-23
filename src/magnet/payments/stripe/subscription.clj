;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.subscription
  (:require [magnet.payments.core :as core]
            [magnet.payments.util :as util]
            [magnet.payments.stripe.connector])
  (:import [magnet.payments.stripe.connector Stripe]))

(defn create-subscription [stripe-record subscription]
  (-> stripe-record
      (util/do-request {:method :post
                        :url "/subscriptions"
                        :form-params subscription})
      (util/default-response :subscription)))

(defn get-subscription [stripe-record subscription-id]
  (-> stripe-record
      (util/do-request {:method :get
                        :url (str "/subscriptions/" subscription-id)})
      (util/default-response :subscription)))

(defn update-subscription [stripe-record subscription-id subscription]
  (-> stripe-record
      (util/do-request {:method :post
                        :url (str "/subscriptions/" subscription-id)
                        :form-params subscription})
      (util/default-response :subscription)))

(defn get-all-subscriptions [stripe-record opt-args]
  (let [{:keys [status body]}
        (util/do-request stripe-record {:method :get
                                        :url "/subscriptions"
                                        :form-params opt-args})]
    (if (= 200 status)
      {:success? true
       :subscriptions (:data body)}
      {:success? false
       :reason (-> body :type keyword)
       :error-details body})))

(defn cancel-subscription [stripe-record subscription-id]
  (-> stripe-record
      (util/do-request {:method :delete
                        :url (str "/subscriptions/" subscription-id)})
      (util/default-response :subscription-canceled)))

(extend-protocol core/Subscriptions
  Stripe
  (create-subscription [this subscription]
    (create-subscription this subscription))
  (get-subscription [this subscription-id]
    (get-subscription this subscription-id))
  (get-all-subscriptions [this opt-args]
    (get-all-subscriptions this opt-args))
  (update-subscription [this subscription-id subscription]
    (update-subscription this subscription-id subscription))
  (cancel-subscription [this subscription-id]
    (cancel-subscription this subscription-id)))
