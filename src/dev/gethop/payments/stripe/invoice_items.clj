(ns dev.gethop.payments.stripe.invoice-items
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const ^:private url-path "/invoiceitems")
(def ^:const ^:private response-path [:invoiceitems :body])

(def ^:const api-definition
  {:create {:method :post
            :url url-path
            :response response-path}
   :get {:method :get
         :url #(str url-path "/" %)
         :response response-path}
   :get-all {:method :get
             :url url-path
             :response response-path}
   :update {:method :post
            :url #(str url-path "/" %)
            :response [:invoice :body]}
   :delete {:method :delete
            :url #(str url-path "/" %)
            :response [:invoice :body]}})

(extend-protocol core/InvoiceItems
  Stripe
  (create-invoice-item [this invoice-item]
    (execute this (:create api-definition) {:entity invoice-item}))
  (update-invoice-items [this invoice-item-id invoice-item]
    (execute this (:update api-definition) {:entity invoice-item
                                            :path-params [invoice-item-id]}))
  (get-invoice-item [this invoice-item-id]
    (execute this (:get api-definition) {:path-params [invoice-item-id]}))
  (get-all-invoice-items [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args}))
  (delete-invoice-item [this invoice-item-id]
    (execute this (:delete api-definition) {:path-params [invoice-item-id]})))