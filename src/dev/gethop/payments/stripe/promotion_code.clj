;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.promotion-code
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const ^:private url-path "/promotion_codes")
(def ^:const ^:private response-path [:promotion_code :body])

(defn- url-fn [id] (str url-path "/" id))

(def ^:const api-definition
  {:create {:method :post
            :url url-path
            :response response-path}
   :get {:method :get
         :url url-fn
         :response response-path}
   :get-all {:method :get
             :url url-path
             :response response-path}
   :update {:method :post
            :url url-fn
            :response response-path}})

(extend-protocol core/PromotionCode
  Stripe
  (create-promotion-code [this coupon]
    (execute this (:create api-definition) {:entity coupon}))
  (update-promotion-code [this coupon-id coupon]
    (execute this (:update api-definition) {:entity coupon
                                            :path-params [coupon-id]}))
  (get-promotion-code [this coupon-id]
    (execute this (:get api-definition) {:path-params [coupon-id]}))
  (get-all-promotion-codes [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args})))