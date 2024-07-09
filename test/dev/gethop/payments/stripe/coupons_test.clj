;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.coupons-test
  (:require [clojure.test :refer :all]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe]
            [dev.gethop.payments.stripe.test-utils :as test-utils]))

(def ^:private ^:const coupon-to-create
  {:duration "repeating"
   :duration_in_months 3
   :name "Test coupon"
   :percent_off 20.0})

(deftest ^:integration create-coupon
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Create coupon"
      (let [{:keys [success? coupon]}
            (core/create-coupon payment-adapter coupon-to-create)]
        (is success?)
        (is (map? coupon))
        (is (= (select-keys coupon (keys coupon-to-create))
               coupon-to-create))
        (core/delete-coupon payment-adapter (:id coupon))))))

(deftest ^:integration update-coupon
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Update coupon"
      (let [{{:keys [id]} :coupon} (core/create-coupon payment-adapter coupon-to-create)
            update-coupon-duration (core/update-coupon payment-adapter id {:duration_in_months 4})
            new-name "Test coupon updated"
            update-coupon-name (core/update-coupon payment-adapter id {:name new-name})]
        (is (false? (:success? update-coupon-duration)))
        (is (:success? update-coupon-name))
        (is (= (get-in update-coupon-name [:coupon :name]) new-name))
        (core/delete-coupon payment-adapter id)))))

(deftest ^:integration get-coupon
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Get coupon"
      (let [{{:keys [id]} :coupon} (core/create-coupon payment-adapter coupon-to-create)
            {:keys [success? coupon]} (core/get-coupon payment-adapter id)]
        (is success?)
        (is (= (:id coupon) id))
        (core/delete-coupon payment-adapter id)))))

(deftest ^:integration get-all-coupons
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Get all coupons"
      (let [now (test-utils/now-unix-timestamp)
            {{:keys [id]} :coupon} (core/create-coupon payment-adapter coupon-to-create)

            {:keys [success?] {:keys [data] :as coupon} :coupon}
            (core/get-all-coupons payment-adapter {:created {:gte now}})]
        (is success?)
        (is (map? coupon))
        (is (vector? data))
        (is (= id (get-in data [0 :id])))
        (core/delete-coupon payment-adapter id)))))

(deftest ^:integration delete-coupon
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Delete coupon"
      (let [{{:keys [id]} :coupon} (core/create-coupon payment-adapter coupon-to-create)

            {:keys [success?] {:keys [deleted]} :coupon}
            (core/delete-coupon payment-adapter id)]
        (is success?)
        (is deleted)))

    (testing "Not successfull delete coupon"
      (is (-> (core/delete-coupon payment-adapter "not-existing-id")
              :success?
              false?)))))