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
import br.mangabaka.api.dto.MangaMetadata
import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
import br.mangabaka.infrastructure.http.anilist.dto.anilist.MangaPaginatedDto
import br.mangabaka.infrastructure.http.anilist.query.MangaPaginatedQuery
import br.mangabaka.service.external.ExternalMetadataService
import jakarta.ws.rs.BadRequestException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class FetchAnilistMangaDataService(
    private val query: MangaPaginatedQuery = MangaPaginatedQuery(),
    private val assetService: FetchAnilistMangaAssetService = FetchAnilistMangaAssetService()
) : ExternalMetadataService {
    companion object {
        private const val PAGE: Int = 1
        private const val PER_PAGE: Int = 1
    }

    override fun fetchMangaData(mangaName: String): MangaMetadata {
         val mangaMetadata: MangaPaginatedDto = query.queryFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)

        val maxAssets = PER_PAGE * 2
        val assetListUrl: Array<AssetInfo?> = arrayOfNulls(size = maxAssets)

        var index = 0
        for (value in mangaMetadata.page.media) {
            val mangaName = value.title.english ?: value.title.romaji ?: value.title.native ?: "unknown"

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

        val mangaAssets = assetService.mangaAsset(assetListUrl)
        return MangaMetadata(paginationInfo = mangaMetadata, assets = mangaAssets)
    }
}