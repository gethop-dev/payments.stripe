(ns dev.gethop.payments.stripe.usage-record-test
  (:require [clojure.test :refer :all]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe]
            [integrant.core :as ig])
  (:import
   [java.time Instant]
   [java.time.temporal ChronoUnit]))

(def ^:const test-config
  {:api-key (System/getenv "STRIPE_TEST_API_KEY")})

(def ^:const invalid-subscription-item-id "si_invalid_id")

(def ^:const test-plan-data {:amount (rand-int 5000)
                             :currency "eur"
                             :interval "month"
                             :usage_type "metered"
                             :product {:name "Gold special"}})

(defn random-usage-record []
  {:quantity (inc (rand-int 10))
   :timestamp (.getEpochSecond (Instant/now))})

(defn create-test-usage-record [adapter subscription-item-id]
  (:usage-record (core/create-usage-record adapter subscription-item-id (random-usage-record))))

(defn create-subscription []
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
        result (core/create-plan payments-adapter test-plan-data)
        plan-id (-> result :plan :id)]
    (:subscription (core/create-subscription
                    payments-adapter
                    {:customer (->> {:description "customer for someone@example.com"}
                                    (core/create-customer payments-adapter)
                                    :customer
                                    :id)
                     :items {"0" {:plan plan-id}}
                     :trial_period_days 30}))))

(deftest ^:integration create-usage-record
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
        subscription (create-subscription)
        subscription-item-id (-> subscription :items :data first :id)]
    (testing "Create usage record successfully"
      (let [usage-record (random-usage-record)
            result (core/create-usage-record payment-adapter subscription-item-id usage-record)]
        (is (:success? result))
        (is (map? (:usage-record result)))
        (is (and (= (:quantity usage-record) (-> result :usage-record :quantity))
                 (= subscription-item-id (-> result :usage-record :subscription_item))))))
    (testing "Invalid subscription item"
      (let [usage-record (random-usage-record)
            result (core/create-usage-record payment-adapter invalid-subscription-item-id usage-record)]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))
    (testing "Timestamp out of subscription period"
      (let [usage-record (assoc (random-usage-record) :timestamp (.getEpochSecond
                                                                  (.minus (Instant/now) 30 ChronoUnit/MINUTES)))
            result (core/create-usage-record payment-adapter subscription-item-id usage-record)]
        (is (not (:success? result)))
        (is (= :bad-request (:reason result)))))))

(deftest ^:integration get-usage-record-summaries
  (let [payment-adapter (ig/init-key :dev.gethop.payments/stripe test-config)
        subscription (create-subscription)
        subscription-item-id (-> subscription :items :data first :id)
        _ (repeat 2 (create-test-usage-record payment-adapter subscription-item-id))]
    (testing "Get summaries successfully"
      (let [result (core/get-usage-record-summaries payment-adapter subscription-item-id {})]
        (is (:success? result))
        (is (vector? (:summaries result)))))
    (testing "Invalid subscription item id"
      (let [result (core/get-usage-record-summaries payment-adapter invalid-subscription-item-id {})]
        (is (not (:success? result)))
        (is (= :not-found (:reason result)))))))
