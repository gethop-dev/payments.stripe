;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.ephemeral-key-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [integrant.core :as ig]
   [magnet.payments.core :as core]))

(def ^:const test-config
  {:api-key (System/getenv "STRIPE_TEST_API_KEY")
   :api-version (System/getenv "STRIPE_API_VERSION")})

(def ^:const customer-test-data {:name "John Smith"})

(def ^:const non-existing-customer-id "cus_random-text")

(deftest ^:integration ^:test-refresh/focus create-ephemeral-key
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Create customer ephemeral key successfully"
      (let [customer-id (-> (core/create-customer payments-adapter customer-test-data)
                            :customer :id)
            result (core/create-ephemeral-key payments-adapter
                                              {:customer customer-id}
                                              (:api-version test-config))]
        (is (:success? result))
        (is (map? (:data result)))))
    (testing "Create customer ephemeral key with invalid customer id fails"
      (let [result (core/create-ephemeral-key payments-adapter
                                              {:customer non-existing-customer-id}
                                              (:api-version test-config))]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))
