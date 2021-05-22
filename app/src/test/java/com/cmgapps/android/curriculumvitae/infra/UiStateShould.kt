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

package com.cmgapps.android.curriculumvitae.infra

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
@Suppress("BlockingMethodInNonBlockingContext")
internal class UiStateShould {

    private val oneSecond = 1.toDuration(DurationUnit.SECONDS)

    @Nested
    @DisplayName("Flow<T>.asUiStateFlow")
    internal inner class Flow {

        @Test
        fun `return success immediately`() = runBlocking {
            flowOf("Test").asUiStateFlow { this }.test(oneSecond) {
                assertThat(expectItem(), instanceOf(UiState.Success::class.java))
                expectComplete()
            }
        }

        @Test
        fun `emit loading with delay`() = runBlocking {
            channelFlow<String?> {
                Thread.sleep(600)
            }.asUiStateFlow { this }.test(oneSecond) {
                assertThat(expectItem(), instanceOf(UiState.Loading::class.java))
                expectComplete()
            }
        }

        @Test
        fun `emit loading with delay if null`() = runBlocking {
            channelFlow<String?> {
                send(null)
            }.asUiStateFlow { this }.test(oneSecond) {
                assertThat(expectItem(), instanceOf(UiState.Loading::class.java))
                expectComplete()
            }
        }

        @Test
        fun `emit loading with delay before success`() = runBlocking {
            channelFlow {
                withContext(Dispatchers.IO) {
                    send(null)
                    Thread.sleep(600)
                    send("Test")
                }
            }.asUiStateFlow { this }.test(oneSecond) {
                assertThat(expectItem(), instanceOf(UiState.Loading::class.java))
                assertThat(expectItem(), instanceOf(UiState.Success::class.java))
                expectComplete()
            }
        }

        @Test
        fun `emit error`() = runBlocking {
            channelFlow<String> {
                throw IllegalStateException("test")
            }.asUiStateFlow { this }.test {
                assertThat(expectItem(), instanceOf(UiState.Error::class.java))
                expectComplete()
            }
        }
    }

    @Nested
    @DisplayName("(suspend () -> T).asUiStateFlow")
    internal inner class Suspend {

        @Test
        fun `emit success immediately`(): Unit = runBlocking {
            suspend fun test(): String {
                return withContext(Dispatchers.Default) {
                    "Test"
                }
            }

            ::test.asUiStateFlow { this }.test {
                assertThat(expectItem(), instanceOf(UiState.Success::class.java))
                expectComplete()
            }
        }

        @Test
        fun `emit loading with delay before success`(): Unit = runBlocking {

            suspend fun test(): String {
                return withContext(Dispatchers.Default) {
                    Thread.sleep(600)
                    "Test"
                }
            }

            ::test.asUiStateFlow { this }.test {
                assertThat(expectItem(), instanceOf(UiState.Loading::class.java))
                assertThat(expectItem(), instanceOf(UiState.Success::class.java))
                expectComplete()
            }
        }

        @Test
        fun `emit error`() = runBlocking {
            @Suppress("RedundantSuspendModifier")
            suspend fun test(): String {
                throw IllegalStateException()
            }

            ::test.asUiStateFlow { this }.test {
                assertThat(expectItem(), instanceOf(UiState.Error::class.java))
                expectComplete()
            }
        }
    }
}
