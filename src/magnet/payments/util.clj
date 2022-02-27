;; This Source Code Form is subject to the terms of the Mozilla Public
;; License, v. 2.0. If a copy of the MPL was not distributed with this
;; file, You can obtain one at http://mozilla.org/MPL/2.0/

(ns magnet.payments.util
  (:require [clojure.data.json :as json]
            [diehard.core :as dh]
            [org.httpkit.client :as http])
  (:import [java.util UUID]))

(def ^:const gateway-timeout
  "504 Gateway timeout The server, while acting as a gateway or proxy,
  did not receive a timely response from the upstream server specified
  by the URI (e.g. HTTP, FTP, LDAP) or some other auxiliary
  server (e.g. DNS) it needed to access in attempting to complete the
  request."
  504)

(def ^:const bad-gateway
  "502 Bad gateway The server, while acting as a gateway or proxy,
  received an invalid response from the upstream server it accessed in
  attempting to fulfill the request."
  502)

(defn- fallback [value exception]
  (let [status (condp instance? exception
                 ;; Socket layer related exceptions
                 java.net.UnknownHostException :unknown-host
                 java.net.ConnectException :connection-refused
                 ;; HTTP layer related exceptions
                 org.httpkit.client.TimeoutException gateway-timeout
                 org.httpkit.client.AbortException bad-gateway)]
    {:status status}))

(defn- retry-policy [max-retries backoff-ms]
  (dh/retry-policy-from-config
   {:max-retries max-retries
    :backoff-ms backoff-ms
    :retry-on [org.httpkit.client.TimeoutException
               org.httpkit.client.AbortException]}))

(defn default-status-codes [code]
  (cond
    (keyword? code) code
    (and (>= code 200) (< code 300)) :ok
    (= code 400) :bad-request
    (or (= code 401) (= code 403)) :access-denied
    (= code 404) :not-found
    :else :error))

(defn do-request [{:keys [api-key timeout max-retries backoff-ms idempotent-post-reqs?]} req-args]
  (let [req (cond-> req-args
              (and idempotent-post-reqs? (= :post (:method req-args)))
              (assoc-in [:headers "Idempotency-Key"] (str (UUID/randomUUID)))

              (:api-version req-args)
              (assoc-in [:headers "Stripe-Version"] (:api-version req-args))

              true
              (assoc :oauth-token api-key
                     :timeout timeout))]
    (dh/with-retry {:policy (retry-policy max-retries backoff-ms)
                    :fallback fallback}
      (let [{:keys [status body error] :as resp} @(http/request req)]
        (when error
          (throw error))
        (try
          {:status status
           :body (json/read-str body :key-fn keyword :eof-error? false)}
          (catch Exception e
            {:status bad-gateway}))))))
