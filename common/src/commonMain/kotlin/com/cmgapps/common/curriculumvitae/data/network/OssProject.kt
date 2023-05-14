/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 */

package com.cmgapps.common.curriculumvitae.data.network

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class OssProject(
    val name: String,
    val description: String,
    val url: String,
    val topics: List<String>,
    val stars: Int,
    val private: Boolean,
    val fork: Boolean,
    val archived: Boolean,
    val pushedAt: Instant?,
)
