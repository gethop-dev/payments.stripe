;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.promotion-code-test
  (:require [clojure.string :as str]
            [clojure.test :refer :all]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe]
            [dev.gethop.payments.stripe.test-utils :as test-utils]))

(deftest ^:integration create-promotion-code
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Create valid promotion code"
      (let [coupon-id (test-utils/create-coupon+get-id payment-adapter)
            code-to-create "PROMOCODE"

            {:keys [success?]
             {:keys [id code] :as promotion_code} :promotion_code}
            (core/create-promotion-code payment-adapter {:coupon coupon-id
                                                         :code code-to-create})]
        (is success?)
        (is (map? promotion_code))
        (is (str/starts-with? id "promo_"))
        (is (= code-to-create code))
        (core/delete-coupon payment-adapter coupon-id)))

    (testing "Create invalid promotion code"
      (is (-> (core/create-promotion-code payment-adapter {})
              :success?
              false?)))))

(deftest ^:integration update-promotion-code
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Update promotion code"
      (let [coupon-id (test-utils/create-coupon+get-id payment-adapter)
            {{:keys [id]} :promotion_code} (core/create-promotion-code payment-adapter {:coupon coupon-id})

            {:keys [success?] {:keys [active] :as promotion_code} :promotion_code}
            (core/update-promotion-code payment-adapter id {:active false})]
        (is success?)
        (is (map? promotion_code))
        (is (false? active))
        (core/delete-coupon payment-adapter coupon-id)))))

(deftest ^:integration get-promotion-code
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Get promotion code"
      (let [coupon-id (test-utils/create-coupon+get-id payment-adapter)
            {{:keys [id]} :promotion_code} (core/create-promotion-code payment-adapter {:coupon coupon-id})
            {:keys [success? promotion_code]} (core/get-promotion-code payment-adapter id)]
        (is success?)
        (is (map? promotion_code))
        (is (= id (:id promotion_code)))
        (core/delete-coupon payment-adapter coupon-id)))))

(deftest ^:integration get-promotion-codes
  (let [payment-adapter (test-utils/init-payment-adapter)]
    (testing "Get promotion codes"
      (let [coupon-id (test-utils/create-coupon+get-id payment-adapter)
            {{:keys [code]} :promotion_code} (core/create-promotion-code payment-adapter {:coupon coupon-id})
            {:keys [success? promotion_code]} (core/get-all-promotion-codes payment-adapter {:code code})]
        (is success?)
        (is (= (get-in promotion_code [:data 0 :code]) code))
        (core/delete-coupon payment-adapter coupon-id)))))
