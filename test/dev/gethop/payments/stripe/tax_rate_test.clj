(ns dev.gethop.payments.stripe.tax-rate-test
  (:require
   [clojure.string :as str]
   [clojure.test :refer :all]
   [dev.gethop.payments.core :as core]
   [dev.gethop.payments.stripe]
   [dev.gethop.payments.stripe.test-utils :as test-utils]))

(defn- create-tax-rate+get-id [payment-adapter]
  (-> (core/create-tax-rate payment-adapter {:display_name "VAT test"
                                             :inclusive false
                                             :percentage 15
                                             :active false})
      :tax-rates
      :id))

(defn- assert-valid-tax-rate [tax-rate]
  (is (map? tax-rate))
  (is (= (:object tax-rate) "tax_rate"))
  (is (str/starts-with? (:id tax-rate) "txr_")))

(defn- assert-success-response [{:keys [success?]}]
  (is success?))

(deftest ^:integration create-tax-rate
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Create valid tax rate"
      (let [response (core/create-tax-rate payment-adapter {:display_name "VAT test"
                                                            :inclusive false
                                                            :percentage 15
                                                            :active false})]
        (assert-success-response response)
        (assert-valid-tax-rate (:tax-rates response))))

    (testing "Create invalid tax rate"
      (let [response (core/create-tax-rate payment-adapter {:display_name "VAT test"})]
        (is (not (:success? response)))))))

(deftest ^:integration update-tax-rate
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Update valid tax rate"
      (let [tax-rate-id (create-tax-rate+get-id payment-adapter)
            response (core/update-tax-rate payment-adapter tax-rate-id {:display_name "VAT test updated"})]
        (assert-success-response response)
        (is (= (:display_name (:tax-rates response)) "VAT test updated"))))

    (testing "Update invalid tax rate"
      (let [response (core/update-tax-rate payment-adapter "txr_invalid" {:display_name "VAT test updated"})]
        (is (not (:success? response)))))))

(deftest ^:integration get-tax-rate
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Get valid tax rate"
      (let [tax-rate-id (create-tax-rate+get-id payment-adapter)
            response (core/get-tax-rate payment-adapter tax-rate-id)]
        (assert-success-response response)
        (assert-valid-tax-rate (:tax-rates response))
        (is (= (:id (:tax-rates response)) tax-rate-id))))

    (testing "Get invalid tax rate"
      (let [response (core/get-tax-rate payment-adapter "txr_invalid")]
        (is (not (:success? response)))))))

(deftest ^:integration get-tax-rates
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Get all tax rates successfully"
      (create-tax-rate+get-id payment-adapter)
      (let [response (core/get-all-tax-rates payment-adapter {:active false})]
        (assert-success-response response)
        (is (vector? (:tax-rates response)))
        (is (seq (:tax-rates response)))))))
