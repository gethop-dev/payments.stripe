;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.product
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const api-definition
  {:get {:method :get
         :url #(str "/products/" %)
         :response [:product :body]}
   :get-all {:method :get
             :url "/products"
             :response [:products [:body :data]]}
   :create {:method :post
            :url "/products"
            :response [:product :body]}
   :update {:method :post
            :url #(str "/products/" %)
            :response [:product :body]}
   :delete {:method :delete
            :url #(str "/products/" %)
            :response [:product-deleted :body]}})

(extend-protocol core/Product
  Stripe
  (create-product [this product]
    (execute this (:create api-definition) {:entity product}))
  (get-product [this product-id]
    (execute this (:get api-definition) {:path-params [product-id]}))
  (get-all-products [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args}))
  (update-product [this product-id product]
    (execute this (:update api-definition) {:entity product
                                            :path-params [product-id]}))
  (delete-product [this product-id]
    (execute this (:delete api-definition) {:path-params [product-id]})))
