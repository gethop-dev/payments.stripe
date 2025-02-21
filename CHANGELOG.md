# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [UNRELEASED]

## [0.3.10] - 2025-02-21
### Added
- Add support for the Coupons and PromotionCode APIs ([Pull request #17])(https://github.com/gethop-dev/payments.stripe/pull/17))
- Add support for missing PaymentMethods API methods ([Pull request #18])(https://github.com/gethop-dev/payments.stripe/pull/18))
- Add support for Tax Rate API ([Pull request #19])(https://github.com/gethop-dev/payments.stripe/pull/19))

## [0.3.9] - 2024-03-21
### Added
- Add support for Price and InvoiceItem APIs ([Pull request #15](https://github.com/gethop-dev/payments.stripe/pull/15))

### Changed
- Dependencies bump

## [0.3.8] - 2022-05-25
### Changed
- Moving the repository to [gethop-dev](https://github.com/gethop-dev) organization
- CI/CD solution switch from [TravisCI](https://travis-ci.org/) to [GitHub Actions](Ihttps://github.com/features/actions)
- `lein`, `cljfmt` and `eastwood` dependencies bump
- This Changelog file update

### Added
- Source code linting using [clj-kondo](https://github.com/clj-kondo/clj-kondo)

## [0.3.7] - 2022-03-04
### Added
- Add support for Ephemeral Keys ([Pull request #13](https://github.com/gethop-dev/payments.stripe/pull/13))

## [0.3.6] - 2022-03-02
### Changed
- **[BREAKING CHANGE]** Upgraded http-kit dependency to 2.5.3. This change bumps the minimum JVM version from 1.6 to 1.7!

## [0.3.5] - 2021-05-19
## Added
- Add support for managing Tax IDs in Customer API
- Add support for PaymentIntents API ([Pull Request #11](https://github.com/gethop-dev/payments.stripe/pull/11))

## [0.3.4] - 2020-07-28
## Added
- Use idempotency keys in POST requests.
  - This is a configurable option.
- Add method to retrieve upcoming invoices
## Changed
- Add support for optional params to cancel-subscription method

## [0.3.3] - 2020-07-22
## Added
- Add support for Payment Methods API
- Add support for Usage Record API
## Changed
- Add .eastwood to .gitignore

## [0.3.2] - 2020-06-22
## Added
- Add support for verifying webhook headers

## [0.3.1] - 2020-04-02
## Added
- Add support for list-events from Events API

## [0.3.0] - 2020-03-31
## Added
- Checkout Session API

## [0.2.0] - 2019-12-10
### Added
- Products API
- Cards API
- Invoices API (Partially)
### Changed
- Library design to avoid repetitive code (doesn't affect functionality)

## [0.1.0] - 2019-10-31
### Added
- Initial version

[UNRELEASED]:  https://github.com/gethop-dev/payments.stripe/compare/0.3.8...HEAD
[0.3.8]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.3.8
[0.3.7]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.3.7
[0.3.6]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.3.6
[0.3.5]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.3.5
[0.3.4]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.3.4
[0.3.3]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.3.3
[0.3.2]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.3.2
[0.3.1]: https://github.com/gethop-dev/payments.stripe/releases/tag/v0.3.1
[0.3.0]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.3.0
[0.2.0]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.2.0
[0.1.0]: https://github.com/gethop-dev/payments.stripe/releases/tag/0.1.0
