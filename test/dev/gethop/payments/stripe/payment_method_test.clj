;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.payment-method-test
  (:require [clojure.test :refer :all]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe]
            [integrant.core :as ig]))

(def ^:const test-config
  {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-customer-data {:description "customer for someone@example.com"})

(def ^:const test-payment-method-id "pm_card_visa")

(def ^:const invalid-customer-id "cus_invalid_id")

(def ^:const invalid-payment-method-id "pm_invalid_id")

(defn- create-test-customer []
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (:customer (core/create-customer payment-adapter test-customer-data))))

(deftest ^:integration get-payment-method
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get valid payment method"
      (let [result (core/get-payment-method payment-adapter test-payment-method-id)]
        (is (:success? result))
        (is (map? (:payment-method result)))))
    (testing "Get payment method with invalid id"
      (let [result (core/get-payment-method payment-adapter invalid-payment-method-id)]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration attach-payment-method
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
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
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
        customer (create-test-customer)
        payment-method-id (-> (core/attach-payment-method payment-adapter test-payment-method-id (:id customer)) :payment-method :id)]
    (testing "Detach payment method"
      (let [result (core/detach-payment-method payment-adapter payment-method-id)]
        (is (:success? result))
        (is (nil? (-> result :payment-method :customer)))))
    (testing "Detach payment method not attached yet"
      (let [result (core/detach-payment-method payment-adapter test-payment-method-id)]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))

(deftest ^:integration get-customer-payment-methods
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
        customer (create-test-customer)]
    (core/attach-payment-method payment-adapter test-payment-method-id (:id customer))
    (letfn [(assert-successful-response [{:keys [success? payment-methods]}]
              (is success?)
              (is (and (vector? payment-methods)
                       (seq payment-methods))))]
      (testing "Get successfully payment methods"
        (let [result (core/get-customer-payment-methods payment-adapter (:id customer) "card" {})]
          (assert-successful-response result))
        (testing "without type"
          (let [result (core/get-customer-payment-methods payment-adapter (:id customer) nil {})]
            (assert-successful-response result)))))
    (testing "Get payment methods for non existing customer"
      (let [result (core/get-customer-payment-methods payment-adapter invalid-customer-id "card" {})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))
    (testing "Get payment methods for invalid type"
      (let [result (core/get-customer-payment-methods payment-adapter (:id customer) "invalid_type" {})]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))

(deftest ^:integration get-customer-payment-method
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
        {customer-id :id} (create-test-customer)
        {{payment-method-id :id} :payment-method}
        (core/attach-payment-method payment-adapter test-payment-method-id customer-id)]
    (testing "Get successfully customer payment method"
      (let [{:keys [success? payment-method]} (core/get-customer-payment-method payment-adapter customer-id payment-method-id)]
        (is success?)
        (is (map? payment-method))
        (is (seq payment-method))
        (is (= (:id payment-method) payment-method-id))))
    (testing "Get payment method not in customer"
      (let [{:keys [success? reason]} (core/get-customer-payment-method payment-adapter customer-id invalid-payment-method-id)]
        (is (not success?))
        (is (= reason :not-found))))))

(deftest ^:integration create-payment-method
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Creare payment methos successfully"
      (testing "US bank account payment method"
        (let [{:keys [success? payment-method]} (core/create-payment-method payment-adapter {:type "us_bank_account"
                                                                                             :us_bank_account {:account_holder_type "individual"
                                                                                                               :account_number "000123456789"
                                                                                                               :routing_number "110000000"}
                                                                                             :billing_details {:name "John Doe"}})]
          (is success?)
          (is (map? payment-method))
          (is (seq payment-method))))
      (testing "Card payment method"
        (let [{:keys [success? payment-method]} (core/create-payment-method payment-adapter {:type "card"
                                                                                             :card {:token "tok_visa"}})]
          (is success?)
          (is (map? payment-method))
          (is (seq payment-method)))))
    (testing "Error creating payment method"
      (let [{:keys [success? reason]} (core/create-payment-method payment-adapter {:type "card"
                                                                                   :card {:exp_year "999"
                                                                                          :exp_month "11"
                                                                                          :number "4242424242424242"}})]
        (is (not success?))
        (is (= reason :error))))))

(deftest ^:integration update-payment-method
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
        {customer-id :id} (create-test-customer)
        {{payment-method-id :id} :payment-method} (core/attach-payment-method payment-adapter test-payment-method-id customer-id)
        current-year (.get (java.util.Calendar/getInstance) java.util.Calendar/YEAR)]
    (testing "Update payment methos successfully"
      (let [{:keys [success? payment-method]} (core/update-payment-method payment-adapter payment-method-id
                                                                          {:card {:exp_year (inc current-year)
                                                                                  :exp_month 1}})]
        (is success?)
        (is (map? payment-method))
        (is (seq payment-method))))

    (testing "Error updating payment method"
      (let [{:keys [success? reason error-details]} (core/update-payment-method payment-adapter payment-method-id
                                                                                {:card {:exp_year (dec current-year)
                                                                                        :exp_month 1}})]
        (is (not success?))
        (is (= reason :error))
        (is (-> error-details
                :error
                :code
                (= "invalid_expiry_year")))))))

(deftest ^:integration get-payment-methods
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
        {customer-id :id} (create-test-customer)
        {{payment-method-id :id} :payment-method} (core/attach-payment-method payment-adapter test-payment-method-id customer-id)]
    (testing "Get payment methods successfully"
      (let [{:keys [success? payment-methods]} (core/get-payment-methods payment-adapter {:limit 1
                                                                                          :customer customer-id
                                                                                          :type "card"})]
        (is success?)
        (is (vector? payment-methods))
        (is (= (count payment-methods) 1))
        (is (-> payment-methods
                first
                :id
                (= payment-method-id)))))

    (testing "Error in get payment methods"
      (let [{:keys [success? reason]} (core/get-payment-methods payment-adapter {:type "card_typo"})]
        (is (not success?))
        (is (= reason :bad-request))))))
