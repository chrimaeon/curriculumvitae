/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmgapps.ktor.curriculumvitae

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class RootRoutesShould {

    @Test
    fun `return OK on GET`() = withTestApplication(moduleFunction = { module() }) {
        with(handleRequest(HttpMethod.Get, "/")) {
            assertThat(response.status(), `is`(HttpStatusCode.OK))
        }
    }

    @Test
    fun `return html on GET`() = withTestApplication(moduleFunction = { module() }) {
        with(handleRequest(HttpMethod.Get, "/")) {
            assertThat(
                response.content,
                `is`(
                    """
                    |<!DOCTYPE html>
                    |<html>
                    |  <head>
                    |    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet" type="text/css">
                    |    <link href="https://code.getmdl.io/1.3.0/material.light_blue-amber.min.css" rel="stylesheet" type="text/css">
                    |    <link href="http://fonts.googleapis.com/css?family=Roboto:300,400,500,700" rel="stylesheet" type="text/css">
                    |    <script src="https://code.getmdl.io/1.3.0/material.min.js"></script>
                    |    <title>Curriculum Vitae BFF</title>
                    |    <style>.page-content {
                    |margin: 16px;
                    |}
                    |</style>
                    |  </head>
                    |  <body>
                    |    <div class="mdl-layout mdl-js-layout mdl-layout--fixed-header">
                    |      <header class="mdl-layout__header">
                    |        <div class="mdl-layout__header-row"><span class="mdl-layout-title">Curriculum Vitae BFF</span></div>
                    |      </header>
                    |      <main class="mdl-layout__content">
                    |        <div class="page-content">
                    |          <h5 class="headline">Available API's</h5>
                    |          <ul>
                    |            <li><code>GET /profile?lang={lang}</code></li>
                    |          </ul>
                    |        </div>
                    |      </main>
                    |    </div>
                    |  </body>
                    |</html>
                    |
                """.trimMargin()
                )
            )
        }
    }
}
