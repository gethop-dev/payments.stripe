;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.card-test
  (:require [clojure.test :refer :all]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe]
            [integrant.core :as ig])
  (:import [java.util UUID]))

(def ^:const test-config {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(defn create-customer []
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
        result (core/create-customer payments-adapter {:email "hey@invalid.invalid"
                                                       :description "abc"})]
    (-> result :customer :id)))

(def ^:const test-customer-id (create-customer))

(def ^:const test-card-data {:source "tok_mastercard"})

(deftest ^:integration create-card
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Create a card successfully"
      (let [result (core/create-card payments-adapter test-customer-id test-card-data)]
        (is (:success? result))
        (is (map? (:card result)))))))

(deftest ^:integration get-card
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get card successfully"
      (let [card-id (-> (core/create-card payments-adapter test-customer-id test-card-data) :card :id)
            result (core/get-card payments-adapter test-customer-id card-id)]
        (is (:success? result))
        (is (map? (:card result)))))
    (testing "Wrong card-id"
      (let [result (core/get-card payments-adapter test-customer-id (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration get-all-cards
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get cards successfully"
      (let [result (core/get-all-cards payments-adapter test-customer-id {})]
        (is (:success? result))
        (is (vector? (:cards result)))))))

(deftest ^:integration update-card
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Update card successfully"
      (let [card-id (-> (core/create-card payments-adapter test-customer-id test-card-data) :card :id)
            result (core/update-card payments-adapter test-customer-id card-id {:address_city "Oñati"})]
        (is (:success? result))
        (is (map? (:card result)))))
    (testing "Update with wrong card-id"
      (let [result (core/update-card payments-adapter test-customer-id (str (UUID/randomUUID)) {:address_city "Oñati"})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration delete-card
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Delete card successfully"
      (let [card-id (-> (core/create-card payments-adapter test-customer-id test-card-data) :card :id)
            result (core/delete-card payments-adapter test-customer-id card-id)]
        (is (:success? result))
        (is (map? (:card-deleted result)))))
    (testing "Delete non existing card"
      (let [result (core/delete-card payments-adapter test-customer-id (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))
