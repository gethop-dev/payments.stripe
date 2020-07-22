(ns magnet.payments.stripe.payment-method-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [magnet.payments.core :as core]
            [magnet.payments.stripe])
  (:import [java.util UUID]))

(def ^:const test-config
  {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-customer-data {:description "customer for someone@example.com"})

(def ^:const test-payment-method-id "pm_card_visa")

(def ^:const invalid-customer-id "cus_invalid_id")

(def ^:const invalid-payment-method-id "pm_invalid_id")

(defn- create-test-customer []
  (let [payment-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (:customer (core/create-customer payment-adapter test-customer-data))))

(deftest ^:integration get-payment-method
  (let [payment-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Get valid payment method"
      (let [result (core/get-payment-method payment-adapter test-payment-method-id)]
        (is (:success? result))
        (is (map? (:payment-method result)))))
    (testing "Get payment method with invalid id"
      (let [result (core/get-payment-method payment-adapter invalid-payment-method-id)]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration attach-payment-method
  (let [payment-adapter (ig/init-key :magnet.payments/stripe test-config)
        customer (create-test-customer)]
    (testing "Attach a valid payment method to an existing customer"
      (let [result (core/attach-payment-method payment-adapter test-payment-method-id (:id customer))]
        (is (:success? result))
        (is (map? (:payment-method result)))
        (is (= (:id customer) (-> result :payment-method :customer)))))
    (testing "Attach invalid payment method to an existing customer"
      (let [result (core/attach-payment-method payment-adapter invalid-payment-method-id (:id customer))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration detach-payment-method
  (let [payment-adapter (ig/init-key :magnet.payments/stripe test-config)
        customer (create-test-customer)
        payment-method-id (-> (core/attach-payment-method payment-adapter test-payment-method-id (:id customer)) :payment-method :id)]
    (testing "Detach payment method"
      (let [result (core/detach-payment-method payment-adapter payment-method-id)]
        (is (:success? result))
        (is (nil? (-> result :payment-method :customer)))))
    (testing "Detach payment method not attached yet"
      (let [result (core/detach-payment-method payment-adapter test-payment-method-id)]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration get-customer-payment-methods
  (let [payment-adapter (ig/init-key :magnet.payments/stripe test-config)
        customer (create-test-customer)
        payment-method (core/attach-payment-method payment-adapter test-payment-method-id (:id customer))]
    (testing "Get successfully payment methods"
      (let [result (core/get-customer-payment-methods payment-adapter (:id customer) "card" {})]
        (is (:success? result))
        (is (and (vector? (:payment-methods result))
                 (not (empty? (:payment-methods result)))))))
    (testing "Get payment methods for non existing customer"
      (let [result (core/get-customer-payment-methods payment-adapter invalid-customer-id "card" {})]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))
    (testing "Get payment methods for invalid type"
      (let [result (core/get-customer-payment-methods payment-adapter (:id customer) "invalid_type" {})]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))
