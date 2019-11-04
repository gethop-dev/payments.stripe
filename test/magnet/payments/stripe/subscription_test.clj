(ns magnet.payments.stripe.subscription-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [magnet.payments.core :as core]
            [magnet.payments.stripe])
  (:import [java.util UUID]))

(def ^:const test-config
  {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const test-plan-data {:amount (rand-int 5000)
                             :currency "eur"
                             :interval "month"
                             :product {:name "Gold special"}})

(defn get-test-subscription-data []
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)
        plan-id (-> (core/create-plan payments-adapter test-plan-data) :plan :id)]
    {:customer (->> {:description "customer for someone@example.com"}
                    (core/create-customer payments-adapter)
                    :customer
                    :id)
     :items {"0" {:plan plan-id}}
     :trial_period_days 30}))

(deftest ^:integration create-subscription
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Create subscription successfully"
      (let [result (core/create-subscription payments-adapter (get-test-subscription-data))]
        (is (:success? result))
        (is (map? (:subscription result)))))
    (testing "Create subscription with missing required paremeter"
      (let [result (core/create-subscription payments-adapter (dissoc (get-test-subscription-data) :customer))]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))

(deftest ^:integration get-subscription
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Get subscription successfully"
      (let [subscription-id (-> (core/create-subscription payments-adapter (get-test-subscription-data)) :subscription :id)
            result (core/get-subscription payments-adapter subscription-id)]
        (is (:success? result))
        (is (map? (:subscription result)))))
    (testing "Wrong subscription-id"
      (let [result (core/get-subscription payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

;;TODO: do a proper update for subscriptions (passing valid properties to update)
(deftest ^:integration update-subscription
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Update subscription successfully"
      (let [subscription-id (-> (core/create-subscription payments-adapter (get-test-subscription-data)) :subscription :id)
            result (core/update-subscription payments-adapter subscription-id {})]
        (is (:success? result))
        (is (map? (:subscription result)))))
    (testing "Update with wrong subscription-id"
      (let [result (core/update-subscription payments-adapter (str (UUID/randomUUID)) {})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration cancel-subscription
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Delete subscription successfully"
      (let [subscription-id (-> (core/create-subscription payments-adapter (get-test-subscription-data)) :subscription :id)
            result (core/cancel-subscription payments-adapter subscription-id)]
        (is (:success? result))
        (is (map? (:subscription-canceled result)))))
    (testing "Delete non existing subscription"
      (let [result (core/cancel-subscription payments-adapter (str (UUID/randomUUID)))]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))

(deftest ^:integration get-all-subscriptions
  (let [payments-adapter (ig/init-key :magnet.payments/stripe test-config)]
    (testing "Get all subscriptions successfully"
      (let [result (core/get-all-subscriptions payments-adapter {})]
        (is (:success? result))
        (is (vector? (:subscriptions result)))))))
