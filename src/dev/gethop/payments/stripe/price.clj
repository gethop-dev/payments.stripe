(ns dev.gethop.payments.stripe.price
  (:require [dev.gethop.payments.core :as core]
            [dev.gethop.payments.stripe.core :refer [execute]])
  (:import [dev.gethop.payments.stripe.core Stripe]))

(def ^:const ^:private url-path "/prices")
(def ^:const ^:private response-path [:price :body])

(defn- url-fn [id] (str url-path "/" id))

(def ^:const api-definition
  {:create {:method :post
            :url url-path
            :response response-path}
   :update {:method :post
            :url url-fn
            :response response-path}
   :get {:method :get
         :url url-fn
         :response response-path}
   :get-all {:method :get
             :url url-path
             :response response-path}})

(extend-protocol core/Price
  Stripe
  (create-price [this price]
    (execute this (:create api-definition) {:entity price}))
  (update-price [this price-id price]
    (execute this (:update api-definition) {:entity price
                                            :path-params [price-id]}))
  (get-price [this price-id]
    (execute this (:get api-definition) {:path-params [price-id]}))
  (get-all-prices [this opt-args]
    (execute this (:get-all api-definition) {:opt-req-args opt-args})))
