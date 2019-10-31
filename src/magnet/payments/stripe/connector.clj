(ns magnet.payments.stripe.connector)

(defrecord Stripe [api-key timeout max-retries backoff-ms])
