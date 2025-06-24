/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.external.anilist

import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.infrastructure.http.anilist.dto.CoverImageMetadata
import br.mangabaka.infrastructure.http.anilist.dto.FuzzyDateIntMetadata
import br.mangabaka.infrastructure.http.anilist.dto.MangaPaginatedMetadataDto
import br.mangabaka.infrastructure.http.anilist.dto.MediaMetadata
import br.mangabaka.infrastructure.http.anilist.dto.PageInfoMetadata
import br.mangabaka.infrastructure.http.anilist.dto.PageMetadata
import br.mangabaka.infrastructure.http.anilist.dto.StatusManga
import br.mangabaka.infrastructure.http.anilist.dto.TagMetadata
import br.mangabaka.infrastructure.http.anilist.dto.TitleMetadata
import br.mangabaka.infrastructure.http.anilist.query.AnilistMangaPaginatedQuery
import br.mangabaka.service.internal.MangaResolverService.Companion.PAGE
import br.mangabaka.service.internal.MangaResolverService.Companion.PER_PAGE
import kotlinx.serialization.SerializationException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

class FetchAnilistMangaDataServiceTest {
    private val mangaValidMetadata = MangaPaginatedMetadataDto(
        page = PageMetadata(
            pageInfo = PageInfoMetadata(
                currentPage = 1, hasNextPage = false, perPage = 1
            ), media = listOf(
                MediaMetadata(
                    id = 1234,
                    idMal = 5678,
                    status = StatusManga.Finished,
                    chapters = 100,
                    volumes = 10,
                    isAdult = false,
                    averageScore = 85,
                    countryOfOrigin = "JP",
                    format = "MANGA",
                    startDate = FuzzyDateIntMetadata(2020, 1, 1),
                    endDate = FuzzyDateIntMetadata(2021, 12, 31),
                    title = TitleMetadata(
                        romaji = "Romaji Title", english = "English Title", native = "日本語タイトル"
                    ),
                    synonyms = listOf("Alt Name 1", "Alt Name 2"),
                    description = "A fake manga used for testing purposes.",
                    genres = listOf("Action", "Adventure"),
                    tags = listOf(
                        TagMetadata(name = "Shounen", rank = 90), TagMetadata(name = "Comedy", rank = 75)
                    ),
                    coverImage = CoverImageMetadata(
                        large = "https://example.com/cover.jpg", color = "#FFFFFF"
                    ),
                    bannerImage = "https://example.com/banner.jpg",
                    siteUrl = "https://anilist.co/manga/1234"
                )
            )
        )
    )

    private val mangaInvalidMetadata = MangaPaginatedMetadataDto(
        page = PageMetadata(
            pageInfo = PageInfoMetadata(
                currentPage = 1, hasNextPage = false, perPage = 1
            ), media = emptyList()
        )
    )

    private val mockQuery = mock(AnilistMangaPaginatedQuery::class.java)
    private val mangaName = "Manga"

    private val service = FetchAnilistMangaDataService(
        query = mockQuery
    )

    @Test
    fun `test fetchAnilistMangaData successful response`() {
        whenever(
            methodCall = mockQuery.queryAllDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)
        ).thenReturn(mangaValidMetadata)

        whenever(
            methodCall = mockQuery.queryAllDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)
        ).thenReturn(mangaValidMetadata)

        val result = service.fetchMangaData(mangaName = mangaName)

        assertEquals(expected = mangaValidMetadata, actual = result.paginationInfo)
    }

    @Test
    fun `fetchAnilistMangaData throws MetadataException when Empty media`() {
        whenever(
            methodCall = mockQuery.queryAllDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)
        ).thenReturn(mangaInvalidMetadata)

        val exception = assertThrows<MetadataException> {
            service.fetchMangaData(mangaName)
        }

        assertEquals(expected = MetadataErrorCode.ERROR_FIELD_EMPTY, actual = exception.errorCode)
    }

    @Test
    fun `fetchAnilistMangaData throws MetadataException when SerializationException occurs`() {
        whenever(
            methodCall = mockQuery.queryAllDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)
        ).thenThrow(SerializationException(message = "Dados malformados"))

        val exception = assertThrows<MetadataException> {
            service.fetchMangaData(mangaName)
        }

        assertTrue(exception.message?.contains(other = "Dados malformados") == true)
        assertEquals(expected = MetadataErrorCode.ERROR_JSON_MALFORMED, actual = exception.errorCode)
    }
}