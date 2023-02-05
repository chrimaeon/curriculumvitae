# Curriculum Vitae

[![CI](https://github.com/chrimaeon/curriculumvitae/actions/workflows/main.yml/badge.svg)](https://github.com/chrimaeon/curriculumvitae/actions/workflows/main.yml)

![Curriculum Vitae](art/banner.png)

This is a [Kotlin Multiplatform] project.

## [Android](app)

An Android App showcasing Jetpack Compose

## [Wearable](wearable)

An Android Watch App showcasing Jetpack Compose

## [iOS](ios)

A iOS App showcasing integration of Kotlin to Swift and SwiftUI

## [Desktop](desktop)

A Java Desktop App showcasing [Compose for Desktop].

Run `./gradlew :desktop:run` to start the app.

## [Web](web)

A JavaScript Web application showcasing [Compose for Web].

You can start the dev server running `./gradlew :web:jsBrowserDevelopmentRun`

## [Back end](backend)

Ktor Back end for the apps

Run locally with `./gradlew :backend:run` or deploy to [Google Cloud App Engine] with `./gradlew :backend:appengineDeploy`

For API Documentation see [backend/README.md]

## [Common](common)

The `common` project includes all the shared code across the different platforms. To learn more
check out [Kotlin Multiplatform].

## [Common Compose](common-compose)

A shared Jetbrains Compose module for shared `Composables`

## License

```text
Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>

SPDX-License-Identifier: Apache-2.0
```

[backend/README.md]: /backend/README.md

[Kotlin Multiplatform]: https://kotlinlang.org/docs/mpp-intro.html

[Compose for Desktop]: https://www.jetbrains.com/de-de/lp/compose/

[Compose for Web]: https://compose-web.ui.pages.jetbrains.team/

[Google Cloud App Engine]: https://cloud.google.com/appengine/
