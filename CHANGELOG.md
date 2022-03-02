# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [UNRELEASED]

## 0.3.6 - 2022-03-02
### Changed
- **[BREAKING CHANGE]** Upgraded http-kit dependency to 2.5.3. This change bumps the minimum JVM version from 1.6 to 1.7!

## 0.3.5 - 2021-05-19
## Added
- Add support for managing Tax IDs in Customer API
- Add support for PaymentIntents API ([Pull Request #11](https://github.com/magnetcoop/payments.stripe/pull/11))

## 0.3.4 - 2020-07-28
## Added
- Use idempotency keys in POST requests.
  - This is a configurable option.
- Add method to retrieve upcoming invoices
## Changed
- Add support for optional params to cancel-subscription method

## 0.3.3 - 2020-07-22
## Added
- Add support for Payment Methods API
- Add support for Usage Record API
## Changed
- Add .eastwood to .gitignore

## 0.3.2 - 2020-06-22
## Added
- Add support for verifying webhook headers

## 0.3.1 - 2020-04-02
## Added
- Add support for list-events from Events API

## 0.3.0 - 2020-03-31
## Added
- Checkout Session API

## 0.2.0 - 2019-12-10
### Added
- Products API
- Cards API
- Invoices API (Partially)
### Changed
- Library design to avoid repetitive code (doesn't affect functionality)

## 0.1.0 - 2019-10-31
### Added
- Initial version
