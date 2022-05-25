;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.payment-intent-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe])
  (:import [java.util UUID]))

(def ^:const test-config
  {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-payment-intent-data {:amount 2222
                                       :currency "usd"
                                       :payment_method_types ["card"]})

(deftest ^:integration ^:test-refresh/focus create-payment-intent
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Create payment intent successfully"
      (let [result (core/create-payment-intent payments-adapter test-payment-intent-data)]
        (is (:success? result))
        (is (map? (:payment-intent result)))))
    (testing "Create payment intent with missing required paremeter"
      (let [result (core/create-payment-intent payments-adapter (dissoc test-payment-intent-data :amount))]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))

(deftest ^:integration get-payment-intent
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get payment intent successfully"
      (let [payment-intent-id (-> (core/create-payment-intent payments-adapter test-payment-intent-data) :payment-intent :id)
            result (core/get-payment-intent payments-adapter payment-intent-id)]
        (is (:success? result))
        (is (map? (:payment-intent result)))))
    (testing "Wrong payment-intent-id"
      (let [result (core/get-payment-intent payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration update-payment-intent
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Update payment intent successfully"
      (let [payment-intent-id (-> (core/create-payment-intent payments-adapter test-payment-intent-data) :payment-intent :id)
            result (core/update-payment-intent payments-adapter payment-intent-id {:amount 3333 :currency "eur" :payment_method_types ["sepa_debit" "card"]})]
        (is (:success? result))
        (is (map? (:payment-intent result)))))
    (testing "Update with wrong payment-intent-id"
      (let [result (core/update-payment-intent payments-adapter (str (UUID/randomUUID)) {})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration capture-payment-intent
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Capture payment intent successfully"
      (let [payment-intent-id (-> (core/create-payment-intent payments-adapter test-payment-intent-data) :payment-intent :id)
            result (core/capture-payment-intent payments-adapter payment-intent-id {})]
        (is (:success? result))
        (is (map? (:payment-intent-captured result)))))
    (testing "Capture non existing payment intent"
      (let [result (core/capture-payment-intent payments-adapter (str (UUID/randomUUID)) {})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration cancel-payment-intent
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Cancel payment intent successfully"
      (let [payment-intent-id (-> (core/create-payment-intent payments-adapter test-payment-intent-data) :payment-intent :id)
            result (core/cancel-payment-intent payments-adapter payment-intent-id {})]
        (is (:success? result))
        (is (map? (:payment-intent-canceled result)))))
    (testing "Cancel non existing payment intent"
      (let [result (core/cancel-payment-intent payments-adapter (str (UUID/randomUUID)) {})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration confirm-payment-intent
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Confirm payment intent successfully"
      (let [payment-intent-id (-> (core/create-payment-intent payments-adapter test-payment-intent-data) :payment-intent :id)
            result (core/confirm-payment-intent payments-adapter payment-intent-id {})]
        (is (:success? result))
        (is (map? (:payment-intent-confirmed result)))))
    (testing "Confirm non existing payment intent"
      (let [result (core/confirm-payment-intent payments-adapter (str (UUID/randomUUID)) {})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration get-all-payment-intents
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get all payment intents successfully"
      (let [result (core/get-all-payment-intents payments-adapter {})]
        (is (:success? result))
        (is (vector? (:payment-intents result)))))))
