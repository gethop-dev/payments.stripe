(ns magnet.payments.stripe.balance
  (:require [magnet.payments.core :as core]
            [magnet.payments.util :as util]
            [magnet.payments.stripe.connector])
  (:import [magnet.payments.stripe.connector Stripe]))

(defn get-balance [stripe-record]
  (-> stripe-record
      (util/do-request {:method :get
                        :url "/balance"})
      (util/default-response :balance)))

(defn get-balance-transaction [stripe-record bt-id]
  (-> stripe-record
      (util/do-request {:method :get
                        :url (str "/balance/history/" bt-id)})
      (util/default-response :balance-transaction)))

(defn get-all-balance-transactions [stripe-record opt-args]
  (let [{:keys [status body]}
        (util/do-request stripe-record {:method :get
                                        :url "/balance_transactions"
                                        :query-params opt-args})]
    (if (= 200 status)
      {:success? true
       :balance-transactions (:data body)}
      {:success? false
       :reason (-> body :type keyword)
       :error-details body})))

(extend-protocol core/Balance
  Stripe
  (get-balance [this]
    (get-balance this))
  (get-balance-transaction [this bt-id]
    (get-balance-transaction this bt-id))
  (get-all-balance-transactions [this opt-args]
    (get-all-balance-transactions this opt-args)))
