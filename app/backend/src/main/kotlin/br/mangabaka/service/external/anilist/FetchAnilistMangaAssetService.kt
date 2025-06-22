/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.external.anilist

import br.mangabaka.api.dto.AssetInfo
import br.mangabaka.exception.code.http.AssetDownloadErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
import br.mangabaka.infrastructure.http.anilist.query.MangaAssetDownload
import jakarta.ws.rs.core.Response

class FetchAnilistMangaAssetService(
    private val mangaAssetDownload: MangaAssetDownload = MangaAssetDownload()
) {
    fun mangaAsset(assetListUrl: Array<AssetInfo?>): List<DownloadedAssetDto> {
        if (assetListUrl.isEmpty()) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_INVALID_URL.handle(value = "A lista de URL para cover e banner está vázia"),
                errorCode = AssetDownloadErrorCode.ERROR_INVALID_URL, httpError = Response.Status.BAD_GATEWAY
            )
        }

        return assetListUrl.filterNotNull().map { assetInfo ->
            try {
                mangaAssetDownload.fetchAsset(
                    url = assetInfo.url,
                    mangaName = assetInfo.mangaName,
                    assetType = assetInfo.type

                ).also { it.validate() }
            } catch (exception: AssetDownloadException) {
                throw exception
            }
        }.toMutableList()
    }
}