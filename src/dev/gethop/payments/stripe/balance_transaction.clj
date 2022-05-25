;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.balance-transaction
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/balance/history/" %)
         :response [:balance-transaction :body]}
   :get-all {:method :get
             :url "/balance_transactions"
             :response [:balance-transactions [:body :data]]}})

(extend-protocol core/BalanceTransaction
  Stripe
  (get-balance-transaction [this bt-id]
    (execute this (:get api-definition) {:path-params [bt-id]}))
  (get-all-balance-transactions [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args})))
