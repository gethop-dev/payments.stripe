;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.core)

(defprotocol Balance
  (get-balance [this]))

(defprotocol BalanceTransaction
  (get-balance-transaction [this bt-id])
  (get-all-balance-transactions [this opt-args]))

(defprotocol Card
  (create-card [this customer-id card])
  (get-card [this customer-id card-id])
  (get-all-cards [this customer-id opt-args])
  (update-card [this customer-id card-id card])
  (delete-card [this customer-id card-id]))

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
  (delete-customer [this customer-id])
  (get-customer-tax-id [this customer-id tax-id])
  (create-customer-tax-id [this customer-id tax-id])
  (delete-customer-tax-id [this customer-id tax-id]))

(defprotocol Coupons
  (create-coupon [this coupon])
  (update-coupon [this coupon-id coupon])
  (get-coupon [this coupon-id])
  (get-all-coupons [this opt-args])
  (delete-coupon [this coupon-id]))

(defprotocol EphemeralKeys
  (create-ephemeral-key [this customer-id api-version]))

(defprotocol Invoice
  (create-invoice [this invoice])
  (get-invoice [this invoice-id])
  (get-all-invoices [this opt-args])
  (get-upcoming-invoice [this customer-id opt-args])
  (update-invoice [this invoice-id invoice]))

(defprotocol InvoiceItems
  (create-invoice-item [this invoice-item])
  (update-invoice-item [this invoice-item-id invoice-item])
  (get-invoice-item [this invoice-item-id])
  (get-all-invoice-items [this opt-args])
  (delete-invoice-item [this invoice-item-id]))

(defprotocol Price
  (create-price [this price])
  (update-price [this price-id price])
  (get-price [this price-id])
  (get-all-prices [this opt-args]))

(defprotocol Product
  (create-product [this product])
  (get-product [this product-id])
  (get-all-products [this opt-args])
  (update-product [this product-id product])
  (delete-product [this product-id]))

(defprotocol PromotionCode
  (create-promotion-code [this promotion-code])
  (update-promotion-code [this promotion-code-id promotion-code])
  (get-promotion-code [this promotion-code-id])
  (get-all-promotion-codes [this opt-args]))

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
  (cancel-subscription [this subscription-id opt-args]))

(defprotocol PaymentMethod
  (create-payment-method [this payment-method])
  (update-payment-method [this payment-method-id payment-method])
  (get-customer-payment-method [this customer-id payment-method-id])
  (get-payment-method [this payment-method-id])
  (get-customer-payment-methods [this customer-id payment-method-type opt-args])
  (get-payment-methods [this opt-args])
  (attach-payment-method [this payment-method-id customer-id])
  (detach-payment-method [this payment-method-id]))

(defprotocol UsageRecord
  (create-usage-record [this subscription-item-id usage-record])
  (get-usage-record-summaries [this subscription-item-id opt-args]))

(defprotocol Checkout
  (create-checkout-session [this checkout-session]))

(defprotocol Events
  (list-events [this event-types opts-args]))

(defprotocol Webhook
  (verify-header [this payload signature-header secret]))

(defprotocol PaymentIntents
  (create-payment-intent [this payment-intent])
  (get-all-payment-intents [this opt-args])
  (get-payment-intent [this payment-intent-id])
  (update-payment-intent [this payment-intent-id payment-intent])
  (confirm-payment-intent [this payment-intent-id opt-args])
  (capture-payment-intent [this payment-intent-id opt-args])
  (cancel-payment-intent [this payment-intent-id opt-args]))

(defprotocol TaxRate
  (create-tax-rate [this tax-rate])
  (update-tax-rate [this tax-rate-id tax-rate])
  (get-tax-rate [this tax-rate-id])
  (get-all-tax-rates [this opt-args]))
