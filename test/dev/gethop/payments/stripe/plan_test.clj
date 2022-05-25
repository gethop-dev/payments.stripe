;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.plan-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe])
  (:import [java.util UUID]))

(def ^:const test-config
  {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-plan-data {:amount (rand-int 5000)
                             :currency "eur"
                             :interval "month"
                             :product {:name "Gold special"}})

(deftest ^:integration create-plan
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Create plan successfully"
      (let [result (core/create-plan payments-adapter test-plan-data)]
        (is (:success? result))
        (is (map? (:plan result)))))
    (testing "Create plan with missing required parameter"
      (let [result (core/create-plan payments-adapter (dissoc test-plan-data :interval))]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))

(deftest ^:integration get-plan
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get a plan successfully"
      (let [plan-id (-> (core/create-plan payments-adapter test-plan-data) :plan :id)
            result (core/get-plan payments-adapter plan-id)]
        (is (:success? result))
        (is (map? (:plan result)))))
    (testing "Wrong plan-id"
      (let [result (core/get-plan payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration update-plan
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Update plan successfully"
      (let [plan-id (-> (core/create-plan payments-adapter test-plan-data) :plan :id)
            result (core/update-plan payments-adapter plan-id {:nickname "Test"})]
        (is (:success? result))
        (is (map? (:plan result)))))
    (testing "Update with wrong plan-id"
      (let [result (core/update-plan payments-adapter (str (UUID/randomUUID)) {:nickname "Test"})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration delete-plan
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Delete plan successfully"
      (let [plan-id (-> (core/create-plan payments-adapter test-plan-data) :plan :id)
            result (core/delete-plan payments-adapter plan-id)]
        (is (:success? result))
        (is (map? (:plan-deleted result)))))
    (testing "Delete non existing plan"
      (let [result (core/delete-plan payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration get-all-plans
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)]
    (testing "Get all plans successfully"
      (let [result (core/get-all-plans payments-adapter {})]
        (is (:success? result))
        (is (vector? (:plans result)))))))
