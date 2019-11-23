;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.charge
  (:require [magnet.payments.core :as core]
            [magnet.payments.util :as util]
            [magnet.payments.stripe.connector])
  (:import [magnet.payments.stripe.connector Stripe]))

(defn create-charge [stripe-record charge]
  (-> stripe-record
      (util/do-request {:method :post
                        :url "/charges"
                        :form-params charge})
      (util/default-response :charge)))

(defn get-charge [stripe-record charge-id]
  (-> stripe-record
      (util/do-request {:method :get
                        :url (str "/charges/" charge-id)})
      (util/default-response :charge)))

(defn update-charge [stripe-record charge-id charge]
  (-> stripe-record
      (util/do-request {:method :post
                        :url (str "/charges/" charge-id)
                        :form-params charge})
      (util/default-response :charge)))

(defn get-all-charges [stripe-record opt-args]
  (let [{:keys [status body]}
        (util/do-request stripe-record {:method :get
                                        :url "/charges"
                                        :form-params opt-args})]
    (if (= 200 status)
      {:success? true
       :charges (:data body)}
      {:success? false
       :reason (-> body :type keyword)
       :error-details body})))

(extend-protocol core/Charge
  Stripe
  (create-charge [this charge]
    (create-charge this charge))
  (get-charge [this charge-id]
    (get-charge this charge-id))
  (get-all-charges [this opt-args]
    (get-all-charges this opt-args))
  (update-charge [this charge-id charge]
    (update-charge this charge-id charge)))
