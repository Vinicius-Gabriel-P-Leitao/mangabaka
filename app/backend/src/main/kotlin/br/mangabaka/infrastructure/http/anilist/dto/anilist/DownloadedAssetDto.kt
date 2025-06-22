/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.dto.anilist

import br.mangabaka.api.dto.AssetType
import br.mangabaka.exception.code.http.AssetDownloadErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import jakarta.ws.rs.core.Response

data class DownloadedAssetDto(
    val filename: String,
    val mediaType: String,
    val content: ByteArray,
    val assetType: AssetType
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DownloadedAssetDto

        if (filename != other.filename) return false
        if (mediaType != other.mediaType) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }

    fun validate() {
        if (filename.isBlank()) throw AssetDownloadException(
            message = AssetDownloadErrorCode.ERROR_INVALID_URL.handle(value = "Nome do arquivo ausente: $filename"),
            errorCode = AssetDownloadErrorCode.ERROR_INVALID_URL, httpError = Response.Status.SERVICE_UNAVAILABLE
        )

        if (mediaType.isBlank()) throw AssetDownloadException(
            message = AssetDownloadErrorCode.ERROR_INVALID_URL.handle(value = "MediaType ausente: $filename"),
            errorCode = AssetDownloadErrorCode.ERROR_INVALID_URL, httpError = Response.Status.SERVICE_UNAVAILABLE
        )

        if (content.isEmpty()) throw AssetDownloadException(
            message = AssetDownloadErrorCode.ERROR_INVALID_URL.handle(value = "Sem conteúdo no asset: $filename"),
            errorCode = AssetDownloadErrorCode.ERROR_INVALID_URL, httpError = Response.Status.SERVICE_UNAVAILABLE
        )
    }
}
