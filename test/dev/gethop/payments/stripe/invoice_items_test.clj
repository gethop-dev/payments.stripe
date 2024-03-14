(ns dev.gethop.payments.stripe.invoice-items-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe]
            [dev.gethop.payments.stripe.test-utils :as test-utils]))

(defn- get-new-invoice-item-id [payment-adapter]
  (let [customer-id (test-utils/create-test-customer+get-id payment-adapter)
        price-id (test-utils/create-price+get-id payment-adapter)]
    (-> (core/create-invoice-item payment-adapter {:customer customer-id
                                                   :price price-id})
        :invoiceitems
        :id)))

(deftest ^:integration crete-invoice-item
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Create invoice item"
      (let [customer-id (test-utils/create-test-customer+get-id payment-adapter)
            price-id (test-utils/create-price+get-id payment-adapter)
            {:keys [success? invoiceitems]}
            (core/create-invoice-item payment-adapter {:customer customer-id
                                                       :price price-id})]
        (is success?)
        (is (map? invoiceitems))
        (is (str/starts-with? (:id invoiceitems) "ii"))))))

(deftest ^:integration update-invoice-item
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Update invoice item"
      (let [id (get-new-invoice-item-id payment-adapter)
            new-desc "updating ii"
            {:keys [success? invoiceitems]}
            (core/update-invoice-item payment-adapter id {:description new-desc})]
        (is success?)
        (is (map? invoiceitems))
        (is (= new-desc (:description invoiceitems)))))))

(deftest ^:integration get-invoice-item
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Get invoice item"
      (let [id (get-new-invoice-item-id payment-adapter)
            {:keys [success? invoiceitems]} (core/get-invoice-item payment-adapter id)]
        (is success?)
        (is (map? invoiceitems))
        (is (= id (:id invoiceitems)))))
    (testing "Get not existing invoice item"
      (-> (core/get-invoice-item payment-adapter "not-existing-id")
          :success?
          false?
          is))))

(deftest ^:integration get-invoice-items
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Get invoice items"
      (let [now (test-utils/now-unix-timestamp)
            id (get-new-invoice-item-id payment-adapter)
            {success? :success?  {:keys [data] :as invoiceitems} :invoiceitems}
            (core/get-all-invoice-items payment-adapter {:created {:gte now}})]
        (is success?)
        (is (map? invoiceitems))
        (is (vector? data))
        (is (= id (get-in data [0 :id])))))))

(deftest ^:integration delete-invoice-item
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Successful delete invoice item"
      (let [id (get-new-invoice-item-id payment-adapter)
            {:keys [success?]} (core/delete-invoice-item payment-adapter id)]
        (is success?)))
    (testing "Not successful delete invoice item"
      (let [{:keys [success?]} (core/delete-invoice-item payment-adapter "not-existing-id")]
        (is (false? success?))))))
