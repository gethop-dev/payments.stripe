;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.coupons
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const ^:private url-path "/coupons")
(def ^:const ^:private response-path [:coupon :body])

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
            :response response-path}
   :delete {:method :delete
            :url url-fn
            :response response-path}})

(extend-protocol core/Coupons
  Stripe
  (create-coupon [this coupon]
    (execute this (:create api-definition) {:entity coupon}))
  (update-coupon [this coupon-id coupon]
    (execute this (:update api-definition) {:entity coupon
                                            :path-params [coupon-id]}))
  (get-coupon [this coupon-id]
    (execute this (:get api-definition) {:path-params [coupon-id]}))
  (get-all-coupons [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args}))
  (delete-coupon [this coupon-id]
    (execute this (:delete api-definition) {:path-params [coupon-id]})))