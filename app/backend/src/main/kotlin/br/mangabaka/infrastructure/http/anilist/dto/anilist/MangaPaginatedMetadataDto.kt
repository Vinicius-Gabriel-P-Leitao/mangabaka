/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.dto.anilist

import br.mangabaka.infrastructure.http.anilist.dto.serializable.Status
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangaPaginatedMetadataDto(
    @SerialName("Page") val page: PageMetadata
)

@Serializable
data class PageMetadata(
    val pageInfo: PageInfoMetadata,
    val media: List<MediaMetadata>
)

@Serializable
data class PageInfoMetadata(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val perPage: Int,
)

@Serializable
data class MediaMetadata(
    val id: Int,
    val idMal: Int?,
    val status: Status,
    val chapters: Int?,
    val volumes: Int?,
    val isAdult: Boolean,
    val averageScore: Int?,
    val countryOfOrigin: String,
    val format: String,
    val startDate: FuzzyDateIntMetadata,
    val endDate: FuzzyDateIntMetadata,
    val title: TitleMetadata,
    val synonyms: List<String>,
    val description: String?,
    val genres: List<String>,
    val tags: List<TagMetadata>,
    val coverImage: CoverImageMetadata,
    val bannerImage: String?,
    val siteUrl: String?
)

@Serializable
data class TitleMetadata(
    val romaji: String?,
    val english: String?,
    val native: String?
)

@Serializable
data class FuzzyDateIntMetadata(
    val year: Int?,
    val month: Int?,
    val day: Int?
)

@Serializable
data class TagMetadata(
    val name: String,
    val rank: Int
)

@Serializable
data class CoverImageMetadata(
    val large: String?,
    val color: String?
)