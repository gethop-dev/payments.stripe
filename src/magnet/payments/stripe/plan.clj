;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.plan
  (:require [magnet.payments.core :as core]
            [magnet.payments.stripe.core :refer [execute]])
  (:import [magnet.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/plans/" %)
         :response [:plan :body]}
   :get-all {:method :get
             :url "/plans"
             :response [:plans [:body :data]]}
   :create {:method :post
            :url "/plans"
            :response [:plan :body]}
   :update {:method :post
            :url #(str "/plans/" %)
            :response [:plan :body]}
   :delete {:method :delete
            :url #(str "/plans/" %)
            :response [:plan-deleted :body]}})

(extend-protocol core/Plans
  Stripe
  (create-plan [this plan]
    (execute this (:create api-definition) {:entity plan}))
  (get-plan [this plan-id]
    (execute this (:get api-definition) {:path-params [plan-id]}))
  (get-all-plans [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args}))
  (delete-plan [this plan-id]
    (execute this (:delete api-definition) {:path-params [plan-id]}))
  (update-plan [this plan-id plan]
    (execute this (:update api-definition) {:path-params [plan-id]
                                            :entity plan})))
