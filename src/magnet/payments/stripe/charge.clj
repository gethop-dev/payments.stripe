;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.charge
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/charges/" %)
         :response [:charge :body]}
   :get-all {:method :get
             :url "/charges"
             :response [:charges [:body :data]]}
   :create {:method :post
            :url "/charges"
            :response [:charge :body]}
   :update {:method :post
            :url #(str "/charges/" %)
            :response [:charge :body]}})

(extend-protocol core/Charge
  Stripe
  (create-charge [this charge]
    (execute this (:create api-definition) {:entity charge}))
  (get-charge [this charge-id]
    (execute this (:get api-definition) {:path-params [charge-id]}))
  (get-all-charges [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args}))
  (update-charge [this charge-id charge]
    (execute this (:update api-definition) {:entity charge
                                            :path-params [charge-id]})))
