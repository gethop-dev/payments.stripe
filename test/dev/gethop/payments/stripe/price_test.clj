(ns dev.gethop.payments.stripe.price-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.test-utils :as test-utils]))

(defn- get-new-price-id [payment-adapter]
  (-> (core/create-price payment-adapter {:currency "eur"
                                          :unit_amount 1000
                                          :product_data {:name "Test product"}})
      :price
      :id))

(deftest ^:integration create-price
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Create valid price"
      (let [{:keys [success? price]}
            (core/create-price payment-adapter {:currency "eur"
                                                :unit_amount 1000
                                                :product_data {:name "Test product"}})]
        (is success?)
        (is (map? price))
        (is (= (:object price) "price"))
        (is (str/starts-with? (:id price) "price"))))
    (testing "Create invalid price"
      (-> (core/create-price payment-adapter {:currency "eur"
                                              :unit_amount 1000})
          :success?
          false?
          is))))

(deftest ^:integration update-price
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Update price"
      (let [id (get-new-price-id payment-adapter)
            {:keys [success? price]} (core/update-price payment-adapter id {:active false})]
        (is success?)
        (is (map? price))
        (is (false? (:active price)))))
    (testing "Update not existing price"
      (-> (core/update-price payment-adapter "not-existing-id" {:active false})
          :success?
          false?
          is))))

(deftest ^:integration get-price
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Update price"
      (let [id (get-new-price-id payment-adapter)
            {:keys [success? price]} (core/get-price payment-adapter id)]
        (is success?)
        (is (map? price))
        (is (= (:id price) id))))
    (testing "Get not existing price"
      (-> (core/get-price payment-adapter "not-existing-id")
          :success?
          false?
          is))))

(deftest ^:integration get-all-prices
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Get all prices"
      (let [now (test-utils/now-unix-timestamp)
            id (get-new-price-id payment-adapter)
            {success? :success? {:keys [data] :as price} :price}
            (core/get-all-prices payment-adapter {:created {:gte now}})]
        (is success?)
        (is (map? price))
        (is (vector? data))
        (is (= id (get-in data [0 :id])))))))
