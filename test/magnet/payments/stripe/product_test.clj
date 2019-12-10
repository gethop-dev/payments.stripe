;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.product-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [magnet.payments.core :as core]
            [magnet.payments.stripe])
  (:import [java.util UUID]))

(def ^:const test-config {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-product-data {:name "Time"
                                :type "good"
                                :description "Description"})

(deftest ^:integration create-product
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Create a product successfully"
      (let [result (core/create-product payments-adapter test-product-data)]
        (is (:success? result))
        (is (map? (:product result)))))))

(deftest ^:integration get-product
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Get product successfully"
      (let [product-id (-> (core/create-product payments-adapter test-product-data) :product :id)
            result (core/get-product payments-adapter product-id)]
        (is (:success? result))
        (is (map? (:product result)))))
    (testing "Wrong product-id"
      (let [result (core/get-product payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration get-all-products
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Get products successfully"
      (let [result (core/get-all-products payments-adapter {})]
        (is (:success? result))
        (is (vector? (:products result)))))))

(deftest ^:integration update-product
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Update product successfully"
      (let [product-id (-> (core/create-product payments-adapter test-product-data) :product :id)
            result (core/update-product payments-adapter product-id {:name "Test"})]
        (is (:success? result))
        (is (map? (:product result)))))
    (testing "Update with wrong product-id"
      (let [result (core/update-product payments-adapter (str (UUID/randomUUID)) {:name "Test"})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration delete-product
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Delete product successfully"
      (let [product-id (-> (core/create-product payments-adapter test-product-data) :product :id)
            result (core/delete-product payments-adapter product-id)]
        (is (:success? result))
        (is (map? (:product-deleted result)))))
    (testing "Delete non existing product"
      (let [result (core/delete-product payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))
