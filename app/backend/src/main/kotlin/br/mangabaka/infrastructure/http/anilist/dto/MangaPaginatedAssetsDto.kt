/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaPaginatedAssetsDto(
    @SerialName("Page") val page: PageAsset
)

@Serializable
data class PageAsset(
    val pageInfo: PageInfoAsset,
    val media: List<MediaAsset>
)

@Serializable
data class PageInfoAsset(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val perPage: Int,
)

@Serializable
data class MediaAsset(
    val id: Int,
    val idMal: Int?,
    val title: TitleAsset,
    val coverImage: CoverImageAsset,
    val bannerImage: String?,
)

@Serializable
data class TitleAsset(
    val romaji: String?,
    val english: String?,
    val native: String?
)

@Serializable
data class CoverImageAsset(
    val large: String?,
)