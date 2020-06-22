;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.stripe.webhook
  (:require [magnet.payments.core :as core])
  (:import [com.stripe.exception SignatureVerificationException]
           [com.stripe.net Webhook$Signature]
           [magnet.payments.stripe.core Stripe]))

(defn verify-header [payload signature-header secret tolerance]
  (try
    (let [valid? (Webhook$Signature/verifyHeader payload
                                                 signature-header
                                                 secret
                                                 tolerance)]
      {:success? valid?})
    (catch Exception e
      {:success? false
       :error-details {:exception (class e)
                       :message (.getMessage e)}})))

(extend-protocol core/Webhook
  Stripe
  (verify-header [this payload signature-header secret]
    (verify-header payload signature-header secret (:webhook-tolerance this))))
