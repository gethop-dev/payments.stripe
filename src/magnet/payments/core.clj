(ns magnet.payments.core)

(defprotocol Balance
  (get-balance [this])
  (get-balance-transaction [this bt-id])
  (get-all-balance-transactions [this opt-args]))

(defprotocol Charge
  (create-charge [this charge])
  (get-charge [this charge-id])
  (get-all-charges [this opt-args])
  (update-charge [this charge-id charge]))

(defprotocol Customers
  (create-customer [this customer])
  (get-customer [this customer-id])
  (get-all-customers [this opt-args])
  (update-customer [this customer-id customer])
  (delete-customer [this customer-id]))

(defprotocol Plans
  (create-plan [this plan])
  (get-plan [this plan-id])
  (get-all-plans [this opt-args])
  (update-plan [this plan-id plan])
  (delete-plan [this plan-id]))

(defprotocol Subscriptions
  (create-subscription [this subscription])
  (get-subscription [this subscription-id])
  (get-all-subscriptions [this opt-args])
  (update-subscription [this subscription-id subscription])
  (cancel-subscription [this subscription-id]))
