;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.plan
  (:require [magnet.payments.core :as core]
            [magnet.payments.util :as util]
            [magnet.payments.stripe.connector])
  (:import [magnet.payments.stripe.connector Stripe]))

(defn create-plan [stripe-record plan]
  (-> stripe-record
      (util/do-request {:method :post
                        :url "/plans"
                        :form-params plan})
      (util/default-response :plan)))

(defn get-plan [stripe-record plan-id]
  (-> stripe-record
      (util/do-request {:method :get
                        :url (str "/plans/" plan-id)})
      (util/default-response :plan)))

(defn update-plan [stripe-record plan-id plan]
  (-> stripe-record
      (util/do-request {:method :post
                        :url (str "/plans/" plan-id)
                        :form-params plan})
      (util/default-response :plan)))

(defn get-all-plans [stripe-record opt-args]
  (let [{:keys [status body]}
        (util/do-request stripe-record {:method :get
                                        :url "/plans"
                                        :form-params opt-args})]
    (if (= 200 status)
      {:success? true
       :plans (:data body)}
      {:success? false
       :reason (-> body :type keyword)
       :error-details body})))

(defn delete-plan [stripe-record plan-id]
  (-> stripe-record
      (util/do-request {:method :delete
                        :url (str "/plans/" plan-id)})
      (util/default-response :plan-deleted)))

(extend-protocol core/Plans
  Stripe
  (create-plan [this plan]
    (create-plan this plan))
  (get-plan [this plan-id]
    (get-plan this plan-id))
  (get-all-plans [this opt-args]
    (get-all-plans this opt-args))
  (update-plan [this plan-id plan]
    (update-plan this plan-id plan))
  (delete-plan [this plan-id]
    (delete-plan this plan-id)))
