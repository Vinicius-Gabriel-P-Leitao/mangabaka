/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.dto

import br.mangabaka.api.dto.AssetType

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
}
