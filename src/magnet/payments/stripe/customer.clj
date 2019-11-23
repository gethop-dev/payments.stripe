;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.customer
  (:require [magnet.payments.core :as core]
            [magnet.payments.util :as util]
            [magnet.payments.stripe.connector])
  (:import [magnet.payments.stripe.connector Stripe]))

(defn create-customer [stripe-record customer]
  (-> stripe-record
      (util/do-request {:method :post
                        :url "/customers"
                        :form-params customer})
      (util/default-response :customer)))

(defn get-customer [stripe-record customer-id]
  (-> stripe-record
      (util/do-request {:method :get
                        :url (str "/customers/" customer-id)})
      (util/default-response :customer)))

(defn get-all-customers [stripe-record opt-args]
  (let [{:keys [status body]}
        (util/do-request stripe-record {:method :get
                                        :url "/customers"
                                        :form-params opt-args})]
    (if (= 200 status)
      {:success? true
       :customers (:data body)}
      {:success? false
       :reason (-> body :type keyword)
       :error-details body})))

(defn update-customer [stripe-record customer-id customer]
  (-> stripe-record
      (util/do-request {:method :post
                        :url (str "/customers/" customer-id)
                        :form-params customer})
      (util/default-response :customer)))

(defn delete-customer [stripe-record customer-id]
  (-> stripe-record
      (util/do-request {:method :delete
                        :url (str "/customers/" customer-id)})
      (util/default-response :customer-deleted)))

(extend-protocol core/Customers
  Stripe
  (create-customer [this customer]
    (create-customer this customer))
  (get-customer [this customer-id]
    (get-customer this customer-id))
  (get-all-customers [this opt-args]
    (get-all-customers this opt-args))
  (update-customer [this customer-id customer]
    (update-customer this customer-id customer))
  (delete-customer [this customer-id]
    (delete-customer this customer-id)))
