/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.external.anilist

import br.mangabaka.api.dto.AssetType
import br.mangabaka.exception.code.custom.AssetDownloadErrorCode
import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.infrastructure.http.anilist.dto.CoverImageAsset
import br.mangabaka.infrastructure.http.anilist.dto.DownloadedAssetDto
import br.mangabaka.infrastructure.http.anilist.dto.MangaPaginatedAssetsDto
import br.mangabaka.infrastructure.http.anilist.dto.MediaAsset
import br.mangabaka.infrastructure.http.anilist.dto.PageAsset
import br.mangabaka.infrastructure.http.anilist.dto.PageInfoAsset
import br.mangabaka.infrastructure.http.anilist.dto.TitleAsset
import br.mangabaka.infrastructure.http.anilist.query.AnilistMangaAssetDownload
import br.mangabaka.infrastructure.http.anilist.query.AnilistMangaPaginatedQuery
import br.mangabaka.service.internal.MangaResolverService.Companion.PAGE
import br.mangabaka.service.internal.MangaResolverService.Companion.PER_PAGE
import kotlinx.serialization.SerializationException
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FetchAnilistMangaAssetServiceTest {
    private val mangaValidMetadata = MangaPaginatedAssetsDto(
        PageAsset(
            pageInfo = PageInfoAsset(
                currentPage = 1, hasNextPage = false, perPage = 1
            ), media = listOf(
                MediaAsset(
                    id = 1,
                    idMal = 1,
                    TitleAsset(
                        romaji = "Romaji Title", english = "English Title", native = "日本語タイトル"
                    ),
                    CoverImageAsset(large = "https://example.com/cover.jpg"),
                    bannerImage = "https://example.com/banner.jpg",
                )
            )
        )
    )

    private val mangaValidDownloadedAsset = DownloadedAssetDto(
        filename = "onepiece-cover.jpg",
        mediaType = "image/jpeg",
        content = byteArrayOf(1, 2, 3, 4, 5),
        assetType = AssetType.COVER
    )

    private val mangaInvalidMetadataMedia = MangaPaginatedAssetsDto(
        PageAsset(
            pageInfo = PageInfoAsset(
                currentPage = 1, hasNextPage = false, perPage = 1
            ), media = emptyList()
        )
    )

    private val mangaInvalidMetadataFieldMediaAsset = MangaPaginatedAssetsDto(
        PageAsset(
            pageInfo = PageInfoAsset(
                currentPage = 1, hasNextPage = false, perPage = 1
            ), media = listOf(
                MediaAsset(
                    id = 1,
                    idMal = 1,
                    title = TitleAsset(
                        romaji = null,
                        english = "",
                        native = null
                    ),
                    CoverImageAsset(large = ""),
                    bannerImage = ""
                )
            )
        )
    )

    private val mangaInvalidDownloadedAssetData = DownloadedAssetDto(
        filename = "", mediaType = "", content = byteArrayOf(), assetType = AssetType.COVER
    )

    private val queryMock = mock<AnilistMangaPaginatedQuery>()
    private val anilistMangaAssetDownloadMock = mock<AnilistMangaAssetDownload>()
    private val mangaName = "Manga"

    private val service = FetchAnilistMangaAssetService(
        query = queryMock, anilistMangaAssetClientDownload = anilistMangaAssetDownloadMock
    )

    @Test
    fun `fetchAnilistMangaAssets successful response`() {
        whenever(
            methodCall = queryMock.queryAssetDataFactory(
                manga = mangaName, page = PAGE, perPage = PER_PAGE
            )
        ).thenReturn(
            mangaValidMetadata
        )

        whenever(
            methodCall = anilistMangaAssetDownloadMock.fetchAsset(endpoint = any(), mangaName = any(), assetType = any())
        ).thenReturn(mangaValidDownloadedAsset)

        val result = service.fetchMangaData(mangaName)

        assertNotNull(result)
        assertNotNull(result.assets)
        assertTrue(result.assets.isNotEmpty())
    }

    @Test
    fun `fetchAnilistMangaAssets throws empty nameManga`() {
        val exception = assertThrows<AssetDownloadException> {
            service.fetchMangaData("")
        }

        assertTrue(exception.message!!.contains("não pode ser vázio"))
    }

    @Test
    fun `fetchAnilistMangaAssets throws MetadataException when Empty media`() {
        whenever(
            methodCall = queryMock.queryAssetDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)
        ).thenReturn(mangaInvalidMetadataMedia)

        whenever(
            methodCall = anilistMangaAssetDownloadMock.fetchAsset(endpoint = any(), mangaName = any(), assetType = any())
        ).thenReturn(mangaValidDownloadedAsset)

        val exception = assertThrows<MetadataException> {
            service.fetchMangaData(mangaName)
        }

        assertTrue(exception.message?.contains(other = "Nenhuma media foi encontrada") == true)
        assertEquals(expected = MetadataErrorCode.ERROR_FIELD_EMPTY, actual = exception.errorCode)
    }

    @Test
    fun `fetchAnilistMangaAssets throws MetadataException when SerializationException occurs`() {
        whenever(
            methodCall = queryMock.queryAssetDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)
        ).thenThrow(SerializationException(message = "Dados malformados"))

        whenever(
            methodCall = anilistMangaAssetDownloadMock.fetchAsset(endpoint = any(), mangaName = any(), assetType = any())
        ).thenReturn(mangaValidDownloadedAsset)

        val exception = assertThrows<MetadataException> {
            service.fetchMangaData(mangaName)
        }

        assertTrue(exception.message?.contains(other = "Dados malformados") == true)
        assertEquals(expected = MetadataErrorCode.ERROR_JSON_MALFORMED, actual = exception.errorCode)
    }

    @Test
    fun `fetchAnilistMangaAssets throws MetadataException when Empty fields in media`() {
        whenever(
            methodCall = queryMock.queryAssetDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)
        ).thenReturn(mangaInvalidMetadataFieldMediaAsset)

        whenever(
            methodCall = anilistMangaAssetDownloadMock.fetchAsset(endpoint = any(), mangaName = any(), assetType = any())
        ).thenReturn(mangaValidDownloadedAsset)

        val exception = assertThrows<MetadataException> {
            service.fetchMangaData(mangaName)
        }

        assertTrue(exception.message?.contains(other = "Não foi encontrado") == true)
        assertEquals(expected = MetadataErrorCode.ERROR_FIELD_EMPTY, actual = exception.errorCode)
    }

    @Test
    fun `fetchAnilistMangaAssets throws AssetDownloadErrorCode when Empty fields in assets data`() {
        whenever(
            methodCall = queryMock.queryAssetDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)
        ).thenReturn(mangaValidMetadata)

        whenever(
            methodCall = anilistMangaAssetDownloadMock.fetchAsset(endpoint = any(), mangaName = any(), assetType = any())
        ).thenReturn(mangaInvalidDownloadedAssetData)

        val exception = assertThrows<AssetDownloadException> {
            service.fetchMangaData(mangaName)
        }

        assertTrue(exception.message?.contains(other = "Campo de nome") == true)
        assertEquals(expected = AssetDownloadErrorCode.ERROR_EMPTY_DATA, actual = exception.errorCode)
    }
}