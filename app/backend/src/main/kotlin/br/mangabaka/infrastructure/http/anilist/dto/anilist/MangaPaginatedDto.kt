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
data class MangaPaginatedDto(
    @SerialName("Page") val page: Page
)

@Serializable
data class Page(
    val pageInfo: PageInfo,
    val media: List<Media>
)

@Serializable
data class PageInfo(
    val currentPage: Int,
    val hasNextPage: Boolean,
    val perPage: Int,
)

@Serializable
data class Media(
    val id: Int,
    val idMal: Int?,
    val status: Status,
    val chapters: Int?,
    val volumes: Int?,
    val isAdult: Boolean,
    val averageScore: Int?,
    val countryOfOrigin: String,
    val format: String,
    val startDate: FuzzyDateInt,
    val endDate: FuzzyDateInt,
    val title: Title,
    val synonyms: List<String>,
    val description: String?,
    val genres: List<String>,
    val tags: List<Tag>,
    val coverImage: CoverImage,
    val bannerImage: String?,
    val siteUrl: String?
)

@Serializable
data class Title(
    val romaji: String?,
    val english: String?,
    val native: String?
)

@Serializable
data class FuzzyDateInt(
    val year: Int?,
    val month: Int?,
    val day: Int?
)

@Serializable
data class Tag(
    val name: String,
    val rank: Int
)

@Serializable
data class CoverImage(
    val large: String?,
    val color: String?
)