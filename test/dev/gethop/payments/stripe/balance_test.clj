;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.balance-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe])
  (:import [java.util UUID]))

(def ^:const test-config
  {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-balance-transaction-id (System/getenv "STRIPE_TEST_BT_ID"))

(deftest ^:integration get-balance
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get balance successfully"
      (let [result (core/get-balance payments-adapter)]
        (is (:success? result))
        (is (map? (:balance result)))))))

(deftest ^:integration get-balance-transaction
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get balance transaction successfully"
      (let [result (core/get-balance-transaction payments-adapter test-balance-transaction-id)]
        (is (:success? result))
        (is (map? (:balance-transaction result)))))
    (testing "Wrong balance-transaction-id"
      (let [balance-transaction-id (str (UUID/randomUUID))
            result (core/get-balance-transaction payments-adapter balance-transaction-id)]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration get-balance-transactions
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get balance-transactions successfully"
      (let [result (core/get-all-balance-transactions payments-adapter {})]
        (is (:success? result))
        (is (vector? (:balance-transactions result)))))))
