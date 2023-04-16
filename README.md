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

A Java Desktop App showcasing [Compose Multiplatform] for Desktop.

Run `./gradlew :desktop:run` to start the app.

## [Web - HTML](web)

A JavaScript Web application showcasing [Compose Multiplatform] for HTML.

You can start the dev server running `./gradlew :web:jsBrowserDevelopmentRun`

## [Web - Canvas](web-canvas)

> **Note**
> Web support is Experimental

A JavaScript / WebAssembly Browser Application showcasing [Compose Multiplatform] rendered on a Canvas.

Run `./gradlew :web-canvas:jsBrowserDevelopmentRun` to start the JavaScript Application

Run `./gradlew :web-canvas:wasmBrowserDevelopmentRun` to start the WebAssembly Application

>**Note**
> Using experimental Kotlin/Wasm may require enabling experimental features in the target environment.

- **Chrome** 110 or newer: enable **WebAssembly Garbage Collection** at [chrome://flags/#enable-webassembly-garbage-collection](chrome://flags/#enable-webassembly-garbage-collection) or with Chrome 109 or newer, run the program with the `--js-flags=--experimental-wasm-gc` command line argument.
- **Firefox Nightly** 112 or newer: enable **javascript.options.wasm_function_references** and **javascript.options.wasm_gc** at [about:config](about:config).
- **Edge** 109 or newer: run the program with the `--js-flags=--experimental-wasm-gc` command line argument.

For more information see https://kotl.in/wasm_help/.

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

[Compose Multiplatform]: https://compose-web.ui.pages.jetbrains.team/

[Google Cloud App Engine]: https://cloud.google.com/appengine/
