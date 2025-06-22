/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.external.anilist

import br.mangabaka.infrastructure.http.anilist.dto.anilist.*
import br.mangabaka.infrastructure.http.anilist.dto.serializable.Status
import br.mangabaka.infrastructure.http.anilist.query.MangaPaginatedQuery
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import kotlin.test.assertEquals

class FetchAnilistMangaDataServiceTest {
    private val mangaMetadata = MangaPaginatedMetadataDto(
        page = PageMetadata(
            pageInfo = PageInfoMetadata(
                currentPage = 1,
                hasNextPage = false,
                perPage = 1
            ),
            media = listOf(
                MediaMetadata(
                    id = 1234,
                    idMal = 5678,
                    status = Status.Finished,
                    chapters = 100,
                    volumes = 10,
                    isAdult = false,
                    averageScore = 85,
                    countryOfOrigin = "JP",
                    format = "MANGA",
                    startDate = FuzzyDateIntMetadata(2020, 1, 1),
                    endDate = FuzzyDateIntMetadata(2021, 12, 31),
                    title = TitleMetadata(
                        romaji = "Romaji Title",
                        english = "English Title",
                        native = "日本語タイトル"
                    ),
                    synonyms = listOf("Alt Name 1", "Alt Name 2"),
                    description = "A fake manga used for testing purposes.",
                    genres = listOf("Action", "Adventure"),
                    tags = listOf(
                        TagMetadata(name = "Shounen", rank = 90),
                        TagMetadata(name = "Comedy", rank = 75)
                    ),
                    coverImage = CoverImageMetadata(
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
        val mockQuery = mock(MangaPaginatedQuery::class.java)

        `when`(mockQuery.queryAllDataFactory("Test Manga", 1, 1)).thenReturn(mangaMetadata)

        val service = FetchAnilistMangaDataService(
            query = mockQuery
        )

        `when`(mockQuery.queryAllDataFactory(manga = mangaName, page = 1, perPage = 1)).thenReturn(mangaMetadata)

        val result = service.fetchMangaData("Test Manga")

        assertEquals(mangaMetadata, result.paginationInfo)
    }
}