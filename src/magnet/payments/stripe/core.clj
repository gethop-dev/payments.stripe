(ns magnet.payments.stripe.core
  (:require [clojure.walk :as walk]
            [clojure.spec.alpha :as s]
            [magnet.payments.core :as payments.core]
            [magnet.payments.util :as util]))

(def ^:const stripe-api-url "https://api.stripe.com/")

(def ^:const api-version "v1")

(defrecord Stripe [api-key timeout max-retries backoff-ms webhook-tolerance idempotent-post-reqs?])

(s/def ::stripe-record record?)

(s/def ::success? boolean?)
(s/def ::error-details map?)
(s/def ::reason keyword?)

(s/def ::method keyword?)
(s/def ::url (s/or :s string? :fn fn?))
(s/def ::response vector?)
(s/def ::endpoint-definition (s/keys :opt-un [::method
                                              ::url
                                              ::response]))
(s/def ::entity map?)
(s/def ::path-param (s/or :n number? :s string?))
(s/def ::path-params (s/coll-of ::path-param :kind vector?))
(s/def ::opt-req-args map?)
(s/def ::endpoint-data (s/keys :opt-un [::entity
                                        ::path-params
                                        ::opt-req-args]))

(defn construct-url [url path-params]
  (str
   stripe-api-url
   api-version
   (if (fn? url)
     (apply url path-params)
     url)))

(defn construct-response [{:keys [status body] :as response} [r-key r-structure]]
  (if (>= 299 status 200)
    {:success? true
     r-key (get-in response (flatten [r-structure]))}
    {:success? false
     :reason (util/default-status-codes status)
     :error-details body}))

(s/def ::execute-args (s/cat :stripe-record ::stripe-record
                             :endpoint-definition ::endpoint-definition
                             :endpoint-data ::endpoint-data))
(s/def ::execute-ret (s/keys :req-un [::success?]
                             :opt-un [::reason ::error-details]))
(s/fdef execute
  :args ::execute-args
  :ret ::execute-ret)

(defn execute
  [stripe-record endpoint-definition endpoint-data]
  {:pre [(and
          (s/valid? ::stripe-record stripe-record)
          (s/valid? ::endpoint-definition endpoint-definition)
          (s/valid? ::endpoint-data endpoint-data))]}
  (let [{:keys [entity path-params opt-req-args api-version]} endpoint-data
        {:keys [method url entity-key response]} endpoint-definition
        url (construct-url url path-params)
        form-params (or entity opt-req-args)]
    (-> stripe-record
        (util/do-request {:method method
                          :url url
                          :form-params (when form-params
                                         (walk/stringify-keys form-params))
                          :api-version api-version})
        (construct-response response))))
