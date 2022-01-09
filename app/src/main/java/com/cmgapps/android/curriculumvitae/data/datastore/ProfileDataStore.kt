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

package com.cmgapps.android.curriculumvitae.data.datastore

import androidx.datastore.core.Serializer
import com.cmgapps.LogTag
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import com.cmgapps.common.curriculumvitae.data.domain.Address as DomainAddress
import com.cmgapps.common.curriculumvitae.data.domain.Profile as DomainProfile
import com.cmgapps.common.curriculumvitae.data.network.Profile as NetworkProfile

@Suppress("BlockingMethodInNonBlockingContext")
@LogTag("ProfileDataStore")
object ProfileDataStoreSerializer : Serializer<Profile?> {
    override val defaultValue: Profile? = null

    override suspend fun readFrom(input: InputStream): Profile? =
        try {
            Profile.ADAPTER.decode(input)
        } catch (exc: IOException) {
            Timber.tag(LOG_TAG).e(exc)
            null
        }

    override suspend fun writeTo(t: Profile?, output: OutputStream) {
        t?.let { Profile.ADAPTER.encode(output, it) }
    }
}

fun Profile.asDomainModel(): DomainProfile {
    if (address == null) {
        throw NullPointerException("address == null")
    }

    return DomainProfile(
        name = name,
        phone = phone,
        profileImagePath = profile_image_path,
        address = DomainAddress(
            street = address.street,
            city = address.city,
            postalCode = address.postal_code
        ),
        email = email,
        intro = intro
    )
}

fun NetworkProfile.asDataStoreModel(): Profile = Profile(
    name = this@asDataStoreModel.name,
    phone = this@asDataStoreModel.phone,
    profile_image_path = this@asDataStoreModel.profileImagePath,
    address = Address(
        street = this@asDataStoreModel.address.street,
        city = this@asDataStoreModel.address.city,
        postal_code = this@asDataStoreModel.address.postalCode,
    ),
    email = this@asDataStoreModel.email,
    intro = this@asDataStoreModel.intro
)
