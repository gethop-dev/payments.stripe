;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.test-utils
  (:require [clojure.test :refer :all]
            [dev.gethop.payments.core :as core]
            [integrant.core :as ig]))

(def ^:const test-config {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-customer-data {:description "customer for someone@example.com"})

(defn init-payment-adapter []
  (ig/init-key :dev.gethop.payments/stripe test-config))

(defn create-test-customer+get-id [payment-adapter]
  (-> (core/create-customer payment-adapter test-customer-data)
      :customer
      :id))

(defn create-price+get-id [payment-adapter]
  (-> (core/create-price payment-adapter {:currency "eur"
                                          :unit_amount 1000
                                          :product_data {:name "Test product"}})
      :price
      :id))

(defn now-unix-timestamp []
  (int (/ (System/currentTimeMillis) 1000)))
