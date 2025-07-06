/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.service.external.anilist

import br.mangabaka.api.dto.AssetInfo
import br.mangabaka.api.dto.AssetType
import br.mangabaka.api.dto.MangaDataDto
import br.mangabaka.exception.code.custom.AssetDownloadErrorCode
import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.infrastructure.config.singleton.I18n
import br.mangabaka.infrastructure.http.anilist.dto.MangaPaginatedAssetsDto
import br.mangabaka.infrastructure.http.anilist.query.AnilistMangaAssetDownload
import br.mangabaka.infrastructure.http.anilist.query.AnilistMangaPaginatedQuery
import br.mangabaka.service.external.ExternalMetadataService
import br.mangabaka.service.internal.MangaResolverService.Companion.PAGE
import br.mangabaka.service.internal.MangaResolverService.Companion.PER_PAGE
import jakarta.ws.rs.core.Response
import kotlinx.serialization.SerializationException

class FetchAnilistMangaAssetService(
    private val query: AnilistMangaPaginatedQuery = AnilistMangaPaginatedQuery(),
    private val anilistMangaAssetClientDownload: AnilistMangaAssetDownload = AnilistMangaAssetDownload(),
) : ExternalMetadataService {
    override fun fetchMangaData(mangaName: String): MangaDataDto {
        if (mangaName.isEmpty()) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(value = I18n.get("throw.manga.name.empty")),
                errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA,
                httpError = Response.Status.BAD_REQUEST
            )
        }

        val assetListUrl: Array<AssetInfo?> = try {
            val mangaAssetData: MangaPaginatedAssetsDto =
                query.queryAssetDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)

            if (mangaAssetData.page.media.isEmpty()) {
                throw MetadataException(
                    message = MetadataErrorCode.ERROR_EMPTY_FIELD.handle(
                        value = I18n.get("throw.metadata.media.empty", mangaName)
                    ), errorCode = MetadataErrorCode.ERROR_EMPTY_FIELD, httpError = Response.Status.NOT_FOUND
                )
            }

            val maxAssets = PER_PAGE * 2
            val tempAssetList: Array<AssetInfo?> = arrayOfNulls(size = maxAssets)

            var index = 0
            for (value in mangaAssetData.page.media) {
                val mangaName: String = listOf(
                    value.title.english, value.title.romaji, value.title.native
                ).firstOrNull { !it.isNullOrBlank() } ?: throw MetadataException(
                    message = MetadataErrorCode.ERROR_EMPTY_FIELD.handle(value = I18n.get("throw.not.found.manga.name.to.asset")),
                    errorCode = MetadataErrorCode.ERROR_EMPTY_FIELD,
                    httpError = Response.Status.BAD_GATEWAY
                )

                val coverUrl: String? = value.coverImage.large?.takeIf { it.isNotBlank() }
                val bannerUrl: String? = value.bannerImage?.takeIf { it.isNotBlank() }

                if (coverUrl == null && bannerUrl == null) {
                    throw MetadataException(
                        message = MetadataErrorCode.ERROR_EMPTY_FIELD.handle(
                            value = I18n.get(
                                "throw.both.asset.fields.null",
                                mangaName
                            )
                        ),
                        errorCode = MetadataErrorCode.ERROR_EMPTY_FIELD,
                        httpError = Response.Status.BAD_GATEWAY
                    )
                }

                if (bannerUrl != null && index < maxAssets) {
                    tempAssetList[index++] = AssetInfo(url = bannerUrl, mangaName = mangaName, type = AssetType.BANNER)
                }

                if (coverUrl != null && index < maxAssets) {
                    tempAssetList[index++] = AssetInfo(url = coverUrl, mangaName = mangaName, type = AssetType.COVER)
                }
            }

            tempAssetList
        } catch (exception: SerializationException) {
            throw MetadataException(
                message = MetadataErrorCode.ERROR_JSON_MALFORMED.handle(
                    value = I18n.get(
                        "throw.malformed.serialization.json",
                        exception.message ?: I18n.get("throw.unknown.error")
                    )
                ),
                errorCode = MetadataErrorCode.ERROR_JSON_MALFORMED,
                httpError = Response.Status.BAD_REQUEST
            )
        }

        val assetData = assetListUrl.filterNotNull().map { assetInfo ->
            val assetDownload = anilistMangaAssetClientDownload.fetchAsset(
                endpoint = assetInfo.url, mangaName = assetInfo.mangaName, assetType = assetInfo.type
            )

            if (assetDownload.filename.isBlank() || assetDownload.filename.isEmpty()) throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(
                    value = I18n.get("throw.missing.name.field", assetDownload)
                ), errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA, httpError = Response.Status.BAD_GATEWAY
            )

            if (assetDownload.mediaType.isBlank() || assetDownload.mediaType.isEmpty()) throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(
                    value = I18n.get("throw.missing.media.type.field", assetDownload)
                ), errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA, httpError = Response.Status.BAD_GATEWAY
            )

            if (assetDownload.content.isEmpty()) throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(
                    value = I18n.get("throw.missing.contend.field", assetDownload)
                ), errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA, httpError = Response.Status.BAD_GATEWAY
            )

            assetDownload
        }.toMutableList()

        anilistMangaAssetClientDownload.close();

        return MangaDataDto(null, assetData)
    }
}