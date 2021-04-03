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

package com.cmgapps.android.curriculumvitae.test

import com.cmgapp.curriculumvitae.data.Address
import com.cmgapp.curriculumvitae.data.Profile

fun StubProfile() = Profile(
    name = "Firstname Lastname",
    address = Address(
        city = "Graz",
        street = "Street 1",
        postalCode = "8010"
    ),
    email = "me@home.at",
    intro = listOf("Line 1", "Line 2"),
    lang = "de",
    phone = "+12345678",
    profileImageUrl = "http://image.undefined.com/image.jpeg"
)
