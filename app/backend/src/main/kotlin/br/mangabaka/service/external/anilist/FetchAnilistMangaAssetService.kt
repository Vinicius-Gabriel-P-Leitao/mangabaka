/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.external.anilist

import br.mangabaka.api.dto.AssetInfo
import br.mangabaka.api.dto.AssetType
import br.mangabaka.api.dto.MangaDataDto
import br.mangabaka.exception.code.http.AssetDownloadErrorCode
import br.mangabaka.exception.code.http.MetadataErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.infrastructure.http.anilist.dto.anilist.MangaPaginatedAssetsDto
import br.mangabaka.infrastructure.http.anilist.query.MangaAssetDownload
import br.mangabaka.infrastructure.http.anilist.query.MangaPaginatedQuery
import br.mangabaka.service.external.ExternalMetadataService
import br.mangabaka.service.internal.MangaResolverService.Companion.PAGE
import br.mangabaka.service.internal.MangaResolverService.Companion.PER_PAGE
import jakarta.ws.rs.core.Response

class FetchAnilistMangaAssetService(
    private val query: MangaPaginatedQuery = MangaPaginatedQuery(),
    private val mangaAssetDownload: MangaAssetDownload = MangaAssetDownload(),
) : ExternalMetadataService {

    override fun fetchMangaData(mangaName: String): MangaDataDto {
        if (mangaName.isEmpty()) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_INVALID_URL.handle(value = "O nome do mangá não pode ser vázio."),
                errorCode = AssetDownloadErrorCode.ERROR_INVALID_URL, httpError = Response.Status.BAD_GATEWAY
            )
        }

        val mangaAssetData: MangaPaginatedAssetsDto =
            query.queryAssetDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)

        val maxAssets = PER_PAGE * 2
        val assetListUrl: Array<AssetInfo?> = arrayOfNulls(size = maxAssets)

        var index = 0
        for (value in mangaAssetData.page.media) {
            val mangaName =
                value.title.english ?: value.title.romaji ?: value.title.native ?: throw MetadataException(
                    message = MetadataErrorCode.ERROR_FIELD_EMPTY.handle(value = "Não foi possível definir nenhum nome de manga para buscar os assets."),
                    errorCode = MetadataErrorCode.ERROR_FIELD_EMPTY, httpError = Response.Status.BAD_GATEWAY
                )

            val coverUrl: String? = value.coverImage.large
            val bannerUrl: String? = value.bannerImage

            if (bannerUrl != null && index < maxAssets) {
                assetListUrl[index] = AssetInfo(url = bannerUrl, mangaName = mangaName, type = AssetType.BANNER)
                index++
            }

            if (coverUrl != null && index < maxAssets) {
                assetListUrl[index] = AssetInfo(url = coverUrl, mangaName = mangaName, type = AssetType.COVER)
                index++
            }
        }

        val asset = assetListUrl.filterNotNull().map { assetInfo ->
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

        return MangaDataDto(null, asset)
    }
}