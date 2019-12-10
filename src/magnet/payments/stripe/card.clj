;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.card
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]]
            [magnet.payments.util :as util])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/customers/" %1 "/sources/" %2)
         :response [:card :body]}
   :get-all {:method :get
             :url #(str "/customers/" % "/sources")
             :response [:cards [:body :data]]}
   :create {:method :post
            :url #(str "/customers/" % "/sources")
            :response [:card :body]}
   :update {:method :post
            :url #(str "/customers/" %1 "/sources/" %2)
            :response [:card :body]}
   :delete {:method :delete
            :url #(str "/customers/" %1 "/sources/" %2)
            :response [:card-deleted :body]}})

(extend-protocol core/Card
  Stripe
  (create-card [this customer-id card]
    (execute this (:create api-definition) {:path-params [customer-id]
                                            :entity card}))
  (get-card [this customer-id card-id]
    (execute this (:get api-definition) {:path-params [customer-id card-id]}))
  (get-all-cards [this customer-id opt-args]
    (execute this (:get-all api-definition) {:path-params [customer-id]
                                             :opt-req-args opt-args}))
  (update-card [this customer-id card-id card]
    (execute this (:update api-definition) {:entity card
                                            :path-params [customer-id card-id]}))
  (delete-card [this customer-id card-id]
    (execute this (:delete api-definition) {:path-params [customer-id card-id]})))
