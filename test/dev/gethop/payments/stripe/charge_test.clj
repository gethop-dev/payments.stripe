;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.charge-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe])
  (:import [java.util UUID]))

(def ^:const test-config {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-charge-data {:amount (rand-int 3000)
                               :currency "eur"
                               :source "tok_mastercard"
                               :description "Charge for someone@example.com"})

(deftest ^:integration create-charge
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Create a charge successfully"
      (let [result (core/create-charge payments-adapter test-charge-data)]
        (is (:success? result))
        (is (map? (:charge result)))))))

(deftest ^:integration get-charge
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get charge successfully"
      (let [charge-id (-> (core/create-charge payments-adapter test-charge-data) :charge :id)
            result (core/get-charge payments-adapter charge-id)]
        (is (:success? result))
        (is (map? (:charge result)))))
    (testing "Wrong charge-id"
      (let [result (core/get-charge payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration get-all-charges
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get charges successfully"
      (let [result (core/get-all-charges payments-adapter {})]
        (is (:success? result))
        (is (vector? (:charges result)))))))
