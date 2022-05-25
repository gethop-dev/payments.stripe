;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns dev.gethop.payments.stripe.webhook-test
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.webhook :as sut])
  (:import [com.stripe.net Webhook$Util]))

(defn rand-str [len]
  (apply str (take len (repeatedly #(char (+ (rand 26) 65))))))

(defn- generate-signature [secret timestamp payload]
  (let [message (format "%d.%s" timestamp payload)]
    (Webhook$Util/computeHmacSha256 secret message)))

(defn- get-current-seconds []
  (Math/round (/ (System/currentTimeMillis) 1000.0)))

(deftest test-verify-header
  (let [payments-adapter (ig/init-key :dev.gethop.payments/stripe {})]
    (testing "Valid header"
      (let [secret (rand-str 32)
            payload (rand-str (rand-int 1000))
            timestamp (get-current-seconds)
            signature (generate-signature secret timestamp payload)
            header (format "t=%d,v1=%s" timestamp signature)]
        (is (:success? (core/verify-header payments-adapter payload header secret)))))
    (testing "Invalid header"
      (let [secret (rand-str 32)
            payload (rand-str (rand-int 1000))
            timestamp (- (get-current-seconds) 10000)
            signature (generate-signature secret timestamp payload)
            header (format "t=%d,v1=%s" timestamp signature)
            result (core/verify-header payments-adapter payload header secret)]
        (is (not (:success? result)))
        (is (map? (:error-details result)))))))
