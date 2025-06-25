/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.query

import br.mangabaka.api.dto.AssetType
import br.mangabaka.exception.code.custom.AssetDownloadErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.Invocation
import jakarta.ws.rs.client.WebTarget
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever

class AnilistMangaAssetDownloadTest {
    private val mockClient = mock<Client>()
    private val mockTarget = mock<WebTarget>()
    private val mockInvocationBuilder = mock<Invocation.Builder>()
    private val mockResponse = mock<Response>()

    private val endpoint = "https://fakeendpoint/assets/cover.jpg"
    private val mangaName = "Manga"

    @BeforeEach
    fun setup() {
        reset(mockClient, mockTarget, mockInvocationBuilder, mockResponse)
    }

    @Test
    fun `test fetchAsset returns valid DownloadedAssetDto on successful download`() {
        whenever(methodCall = mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(methodCall = mockTarget.request()).thenReturn(mockInvocationBuilder)
        whenever(methodCall = mockInvocationBuilder.get()).thenReturn(mockResponse)
        whenever(methodCall = mockResponse.status).thenReturn(200)
        whenever(methodCall = mockResponse.mediaType).thenReturn(MediaType.valueOf("image/jpeg"))
        whenever(methodCall = mockResponse.readEntity(ByteArray::class.java)).thenReturn(
            byteArrayOf(
                0xFF.toByte(), 0xD9.toByte()
            )
        )

        val mangaAssetDownloadClient = AnilistMangaAssetDownload(mockClient)

        val downloadedAsset = mangaAssetDownloadClient.fetchAsset(endpoint, mangaName, AssetType.COVER)

        assertEquals("manga-cover.jpg", downloadedAsset.filename)
        assertEquals("image/jpeg", downloadedAsset.mediaType)
        assertArrayEquals(byteArrayOf(0xFF.toByte(), 0xD9.toByte()), downloadedAsset.content)
        assertEquals(AssetType.COVER, downloadedAsset.assetType)

        mangaAssetDownloadClient.close()
    }

    @Test
    fun `test fetchAsset throws AssetDownloadException on non-200 status`() {
        whenever(methodCall = mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(methodCall = mockTarget.request()).thenReturn(mockInvocationBuilder)
        whenever(methodCall = mockInvocationBuilder.get()).thenReturn(mockResponse)
        whenever(methodCall = mockResponse.status).thenReturn(404)

        val mangaAssetDownloadClient = AnilistMangaAssetDownload(mockClient)

        val exception = assertThrows<AssetDownloadException> {
            mangaAssetDownloadClient.fetchAsset(endpoint, mangaName, AssetType.COVER)
        }

        assertEquals(AssetDownloadErrorCode.ERROR_CLIENT_STATUS, exception.errorCode)

        mangaAssetDownloadClient.close()
    }

    @Test
    fun `test fetchAsset throws AssetDownloadException on IOException`() {
        whenever(methodCall = mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(methodCall = mockTarget.request()).thenReturn(mockInvocationBuilder)
        whenever(methodCall = mockInvocationBuilder.get()).thenThrow(
            NullPointerException ("Connection reset")
        )

        val mangaAssetDownloadClient = AnilistMangaAssetDownload(mockClient)

        val exception = assertThrows<AssetDownloadException> {
            mangaAssetDownloadClient.fetchAsset(endpoint, mangaName, AssetType.COVER)
        }

        assertEquals(AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION, exception.errorCode)

        mangaAssetDownloadClient.close()
    }

    @Test
    fun `test get throws exception ProcessingException on timeout`() {
        whenever(methodCall = mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(methodCall = mockTarget.request()).thenReturn(mockInvocationBuilder)
        whenever(methodCall = mockInvocationBuilder.get()).thenThrow(
            ProcessingException("timeout")
        )

        val mangaAssetDownloadClient = AnilistMangaAssetDownload(mockClient)

        val exception = assertThrows<AssetDownloadException> {
            mangaAssetDownloadClient.fetchAsset(endpoint = endpoint, mangaName, AssetType.COVER)
        }

        assertEquals(AssetDownloadErrorCode.ERROR_TIMEOUT, exception.errorCode)
    }

    @Test
    fun `test executeQuery throws exception on invalid endpoint`() {
        val mangaAssetDownloadClient = AnilistMangaAssetDownload(mockClient)

        val exception = assertThrows<AssetDownloadException> {
            mangaAssetDownloadClient.fetchAsset(endpoint = "invalid endpoint", mangaName, AssetType.COVER)
        }

        assertEquals(AssetDownloadErrorCode.ERROR_INVALID_URL, exception.errorCode)
    }
}