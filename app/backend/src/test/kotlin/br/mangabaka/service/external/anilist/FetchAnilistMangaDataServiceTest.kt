/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.external.anilist

import br.mangabaka.api.dto.AssetInfo
import br.mangabaka.infrastructure.http.anilist.dto.anilist.*
import br.mangabaka.infrastructure.http.anilist.dto.serializable.Status
import br.mangabaka.infrastructure.http.anilist.query.MangaPaginatedQuery
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import kotlin.test.assertEquals

class FetchAnilistMangaDataServiceTest {
    private val mangaMetadata = MangaPaginatedDto(
        page = Page(
            pageInfo = PageInfo(
                currentPage = 1,
                hasNextPage = false,
                perPage = 1
            ),
            media = listOf(
                Media(
                    id = 1234,
                    idMal = 5678,
                    status = Status.Finished,
                    chapters = 100,
                    volumes = 10,
                    isAdult = false,
                    averageScore = 85,
                    countryOfOrigin = "JP",
                    format = "MANGA",
                    startDate = FuzzyDateInt(2020, 1, 1),
                    endDate = FuzzyDateInt(2021, 12, 31),
                    title = Title(
                        romaji = "Romaji Title",
                        english = "English Title",
                        native = "日本語タイトル"
                    ),
                    synonyms = listOf("Alt Name 1", "Alt Name 2"),
                    description = "A fake manga used for testing purposes.",
                    genres = listOf("Action", "Adventure"),
                    tags = listOf(
                        Tag(name = "Shounen", rank = 90),
                        Tag(name = "Comedy", rank = 75)
                    ),
                    coverImage = CoverImage(
                        large = "https://example.com/cover.jpg",
                        color = "#FFFFFF"
                    ),
                    bannerImage = "https://example.com/banner.jpg",
                    siteUrl = "https://anilist.co/manga/1234"
                )
            )
        )
    )

    @Test
    fun `test fetchMangaData successful response`() {
        val mangaName = "Example Manga"
        val assetListUrl = arrayOfNulls<AssetInfo?>(2)
        val mangaAssets = listOf<DownloadedAssetDto>()

        val mockQuery = mock(MangaPaginatedQuery::class.java)
        val mockAssetService = mock(FetchAnilistMangaAssetService::class.java)

        `when`(mockQuery.queryFactory("Test Manga", 1, 1)).thenReturn(mangaMetadata)
        `when`(mockAssetService.mangaAsset(any())).thenReturn(emptyList())

        val service = FetchAnilistMangaDataService(
            query = mockQuery,
            assetService = mockAssetService
        )

        `when`(mockQuery.queryFactory(manga = mangaName, page = 1, perPage = 1)).thenReturn(mangaMetadata)

        val result = service.fetchMangaData("Test Manga")

        assertEquals(mangaMetadata, result.paginationInfo)
        assertEquals(mangaAssets, result.assets)
    }
}