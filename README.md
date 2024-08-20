# payments.stripe
[![ci-cd](https://github.com/gethop-dev/payments.stripe/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/gethop-dev/payments.stripe/actions/workflows/ci-cd.yml)
[![Clojars Project](https://img.shields.io/clojars/v/dev.gethop/payments.stripe.svg)](https://clojars.org/dev.gethop/payments.stripe)

A [Duct](https://github.com/duct-framework/duct) library that provides an [Integrant](https://github.com/weavejester/integrant) key for interacting with the Stripe API.

## Table of contents
* [Installation](#installation)
* [Usage](#usage)
  * [Configuration](#configuration)
  * [Obtaining a Stripe record](#obtaining-a-stripe-record)
  * [Available methods](#available-methods)

## Installation

[![Clojars Project](https://clojars.org/dev.gethop/payments.stripe/latest-version.svg)](https://clojars.org/dev.gethop/payments.stripe)

## Usage

### Configuration
To use this library add the following key to your configuration:

`:dev.gethop.paymets/stripe`

This key expects a configuration map with one unique mandatory key, plus another three optional ones.
These are the mandatory keys:

* `:api-key` : [API key](https://stripe.com/docs/keys) to authenticate requests

These are the optional keys:
* `:timeout`: Timeout value (in milli-seconds) for an connection attempt with Stripe.
* `:max-retries`: If the connection attempt fails, how many retries we want to attempt before giving up.
* `:backoff-ms`: This is a vector in the form [initial-delay-ms max-delay-ms multiplier] to control the delay between each retry. The delay for nth retry will be (max (* initial-delay-ms n multiplier) max-delay-ms). If multiplier is not specified (or if it is nil), a multiplier of 2 is used. All times are in milli-seconds.
* `:webhook-tolerance` : Used when verifying webhook headers. Maximum difference in seconds allowed between the header's timestamp and the current time. Default value is 300 seconds.
* `:idempotent-post-reqs?`: Boolean value that determines whether idempotency keys should be used in POST requests or not. The default value is `true`.

Key initialization returns a `Stripe` record that can be used to perform the Stripe operations described below.

#### Configuration example
Basic configuration:
```edn
  :dev.gethop.payments/stripe
   {:api-key #duct/env ["STRIPE_API_KEY" Str :or "pk_test_TYooMQauvdEDq54NiTphI7jx"]}
```

Configuration with custom request retry policy:
```edn
  :dev.gethop.payments/stripe
   {:api-key #duct/env ["STRIPE_API_KEY" Str :or "pk_test_TYooMQauvdEDq54NiTphI7jx"]
    :timeout 1000
    :max-retries 3
    :backoff-ms [10 500]}
```

### Obtaining a `Stripe` record

If you are using the library as part of a [Duct](https://github.com/duct-framework/duct)-based project, adding any of the previous configurations to your `config.edn` file will perform all the steps necessary to initialize the key and return a `Stripe` record for the associated configuration. In order to show a few interactive usages of the library, we will do all the steps manually in the REPL.

First we require the relevant namespaces:

```clj
user> (require '[dev.gethop.payments.core :as core]
               '[integrant.core :as ig])
nil
user>
```

Next we create the configuration var holding the Stripe integration configuration details:

```clj
user> (def config {:api-key #duct/env ["STRIPE_API_KEY" Str :or "pk_test_TYooMQauvdEDq54NiTphI7jx"]})
#'user/config
user>
```

Now that we have all pieces in place, we can initialize the `:dev.gethop.payments/stripe` Integrant key to get a `Stripe` record. As we are doing all this from the REPL, we have to manually require `dev.gethop.payments.stripe` namespace, where the `init-key` multimethod for that key is defined (this is not needed when Duct takes care of initializing the key as part of the application start up):

``` clj
user> (require '[dev.gethop.payments.stripe :as stripe])
nil
user>
```

And we finally initialize the key with the configuration defined above, to get our `Stripe` record:

``` clj
user> (def stripe-record (->
                       config
                       (->> (ig/init-key :dev.gethop.payments/stripe))))
#'user/stripe-record
user> stripe-record
#dev.gethop.payments.stripe.Stripe{:api-key #duct/env ["STRIPE_API_KEY" Str :or "pk_test_TYooMQauvdEDq54NiTphI7jx"]
                                   :timeout 2000,
                                   :max-retries 10,
                                   :backoff-ms [500 1000 2.0]}
user>
```
Now that we have our `Stripe` record, we are ready to use the methods defined by the protocols defined in `dev.gethop.payments.core` namespace.

### Available methods

This are the methods available to interact with the Stripe API. The mapping for the methods is one to one, so refer to the Stripe  official documentation for details.

  * [Balance](https://stripe.com/docs/api/balance)
    * [(get-balance stripe-record)](https://stripe.com/docs/api/balance/balance_retrieve)
  * [Balance Transaction](https://stripe.com/docs/api/balance_transactions)
    * [(get-balance-transaction stripe-record bt-id)](https://stripe.com/docs/api/balance_transactions/retrieve)
    * [(get-balance-transactions stripe-record opt-args)](https://stripe.com/docs/api/balance_transactions/list)
  * [Card](https://stripe.com/docs/api/cards)
    * [(create-card stripe-record customer-id card)](https://stripe.com/docs/api/cards/create)
    * [(get-card stripe-record customer-id card-id)](https://stripe.com/docs/api/cards/retrieve)
    * [(get-all-cards stripe-record customer-id opt-args)](https://stripe.com/docs/api/cards/list)
    * [(update-card stripe-record customer-id card-id card)](https://stripe.com/docs/api/cards/update)
    * [(delete-card stripe-record customer-id card-id)](https://stripe.com/docs/api/cards/delete)
  * [Charge](https://stripe.com/docs/api/charges)
    * [(create-charge stripe-record charge)](https://stripe.com/docs/api/charges/create)
    * [(get-charge stripe-record charge-id)](https://stripe.com/docs/api/charges/retrieve)
    * [(get-all-charges stripe-record opt-args)](https://stripe.com/docs/api/charges/list)
    * [(update-charge stripe-record charge-id charge)](https://stripe.com/docs/api/charges/update)
  * [Customer](https://stripe.com/docs/api/customers)
    * [(create-customer stripe-record customer)](https://stripe.com/docs/api/customers/create)
    * [(get-customer stripe-record customer-id)](https://stripe.com/docs/api/customers/retrieve)
    * [(get-all-customers stripe-record opt-args)](https://stripe.com/docs/api/customers/list)
    * [(update-customer stripe-record customer-id customer)](https://stripe.com/docs/api/customers/update)
    * [(delete-customer stripe-record customer-id)](https://stripe.com/docs/api/customers/delete)
      [(get-customer-tax-id stripe-record customer-id tax-id)](https://stripe.com/docs/api/customer_tax_ids/retrieve)
    * [(create-customer-tax-id stripe-record customer-id tax-id)](https://stripe.com/docs/api/customer_tax_ids/create)
    * [(delete-customer-tax-id stripe-record customer-id tax-id)](https://stripe.com/docs/api/customer_tax_ids/delete)
  * Ephemeral Keys
    * (create-ephemeral-key stripe-record {:customer customer-id} *OR* {:issuing_card issuing-card-id})
    
    Does not have offical docs, but there are some examples in some of the guides, such as [this one.]( https://stripe.com/docs/payments/accept-a-payment?platform=react-native#react-native-add-server-endpoint)
  * [Invoice](https://stripe.com/docs/api/invoices)
    * [(create-invoice stripe-record invoice)](https://stripe.com/docs/api/invoices/create)
    * [(get-invoice stripe-record invoice-id)](https://stripe.com/docs/api/invoices/retrieve)
    * [(get-all-invoices stripe-record opt-args)](https://stripe.com/docs/api/invoices/list)
    * [(update-invoice stripe-record invoice-id invoice)](https://stripe.com/docs/api/invoices/update)
    * [(delete-invoice stripe-record invoice-id)](https://stripe.com/docs/api/invoices/delete)
  * [Plan](https://stripe.com/docs/api/plans)
    * [(create-plan stripe-record plan)](https://stripe.com/docs/api/plans/create)
    * [(get-plan stripe-record plan-id)](https://stripe.com/docs/api/plans/retrieve)
    * [(get-all-plans stripe-record opt-args)](https://stripe.com/docs/api/plans/list)
    * [(update-plan stripe-record plan-id plan)](https://stripe.com/docs/api/plans/update)
    * [(delete-plan stripe-record plan-id)](https://stripe.com/docs/api/plans/delete)
  * [Product](https://stripe.com/docs/api/products)
    * [(create-product stripe-record product)](https://stripe.com/docs/api/products/create)
    * [(get-product stripe-record product-id)](https://stripe.com/docs/api/products/retrieve)
    * [(get-all-products stripe-record opt-args)](https://stripe.com/docs/api/products/list)
    * [(update-product stripe-record product-id product)](https://stripe.com/docs/api/products/update)
    * [(delete-product stripe-record product-id)](https://stripe.com/docs/api/products/delete)
  * [Subscription](https://stripe.com/docs/api/subscriptions)
    * [(create-subscription stripe-record subscription)](https://stripe.com/docs/api/subscriptions/create)
    * [(get-subscription stripe-record subscription-id)](https://stripe.com/docs/api/subscriptions/retrieve)
    * [(get-all-subscriptions stripe-record opt-args)](https://stripe.com/docs/api/subscriptions/list)
    * [(update-subscription stripe-record subscription-id subscription)](https://stripe.com/docs/api/subscriptions/update)
    * [(cancel-subscription stripe-record subscription-id)](https://stripe.com/docs/api/subscriptions/cancel)
  * [PaymentMethod](https://stripe.com/docs/api/payment_methods)
    * [(get-payment-method stripe-record payment-method-id)](https://stripe.com/docs/api/payment_methods/retrieve)
    * [(get-customer-payment-methods stripe-record customer-id payment-method-id opt-args)](https://stripe.com/docs/api/payment_methods/list)
    * [(attach-payment-method stripe-record payment-method-id customer-id)](https://stripe.com/docs/api/payment_methods/attach)
    * [(detach-payment-method stripe-record payment-method-id)](https://stripe.com/docs/api/payment_methods/detach)
  * [UsageRecord](https://stripe.com/docs/api/usage_records)
    * [(create-usage-record stripe-record subscription-item-id usage-record)](https://stripe.com/docs/api/usage_records/create)
    * [(get-usage-record-summaries stripe-record subscription-item-id opt-args)](https://stripe.com/docs/api/usage_records/subscription_item_summary_list)
  * [Checkout Session](https://stripe.com/docs/api/checkout/sessions)
    * [(create-checkout-session stripe-record checkout-session)](https://stripe.com/docs/api/checkout/sessions/create)
  * [Events](https://stripe.com/docs/api/events)
    * [(list-events stripe-record event-types opt-args)](https://stripe.com/docs/api/events/list)
  * [Webhook](https://stripe.com/docs/api/webhooks)
    * [(verify-header stripe-record payload signature-header secret)](https://stripe.dev/stripe-java/com/stripe/net/Webhook.Signature.html)
  * [PaymentIntents](https://stripe.com/docs/api/payment_intents)
    * [(create-payment-intent [this payment-intent])](https://stripe.com/docs/api/payment_intents#create_payment_intent)
    * [(get-all-payment-intents [this opt-args])](https://stripe.com/docs/api/payment_intents#list_payment_intents)
    * [(get-payment-intent [this payment-intent-id])](https://stripe.com/docs/api/payment_intents#retrieve_payment_intent)
    * [(update-payment-intent [this payment-intent-id payment-intent])](https://stripe.com/docs/api/payment_intents#update_payment_intent)
    * [(confirm-payment-intent [this payment-intent-id opt-args])](https://stripe.com/docs/api/payment_intents#confirm_payment_intent)
    * [(capture-payment-intent [this payment-intent-id opt-args])](https://stripe.com/docs/api/payment_intents#capture_payment_intent)
    * [(cancel-payment-intent [this payment-intent-id opt-args])](https://stripe.com/docs/api/payment_intents#cancel_payment_intent)

All the responses will include a `:success?` key. When `:success?` is `false`, `:reason` and `error-details` keys will be also included. The possible reasons are: `:bad-request`, `not-found`, `access-denied` and `error`. The `error-details` will include a map with the error information provided by the Stripe API.

## License

Copyright (c) 2024 Biotz, SL.

This Source Code Form is subject to the terms of the Mozilla Public License,
v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain
one at https://mozilla.org/MPL/2.0/
