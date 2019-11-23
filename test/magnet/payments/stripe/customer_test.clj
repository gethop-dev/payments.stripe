;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.customer-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [magnet.payments.core :as core]
            [magnet.payments.stripe])
  (:import [java.util UUID]))

(def ^:const test-config
  {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-customer-data {:description "customer for someone@example.com"})

(deftest ^:integration create-customer
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Create customer successfully"
      (let [result (core/create-customer payments-adapter test-customer-data)]
        (is (:success? result))
        (is (map? (:customer result)))))))

(deftest ^:integration get-customer
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Get customer successfully"
      (let [customer-id (-> (core/create-customer payments-adapter test-customer-data) :customer :id)
            result (core/get-customer payments-adapter customer-id)]
        (is (:success? result))
        (is (map? (:customer result)))))
    (testing "Wrong customer-id"
      (let [result (core/get-customer payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration update-customer
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Update customer successfully"
      (let [customer-id (-> (core/create-customer payments-adapter test-customer-data) :customer :id)
            result (core/update-customer payments-adapter customer-id {:description "test"})]
        (is (:success? result))
        (is (map? (:customer result)))))
    (testing "Update with wrong customer-id"
      (let [result (core/update-customer payments-adapter (str (UUID/randomUUID)) {:description "test"})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration delete-customer
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Delete customer successfully"
      (let [customer-id (-> (core/create-customer payments-adapter test-customer-data) :customer :id)
            result (core/delete-customer payments-adapter customer-id)]
        (is (:success? result))
        (is (map? (:customer-deleted result)))))
    (testing "Delete non existing customer"
      (let [result (core/delete-customer payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration get-all-customers
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Get all customers successfully"
      (let [result (core/get-all-customers payments-adapter {})]
        (is (:success? result))
        (is (vector? (:customers result)))))))
