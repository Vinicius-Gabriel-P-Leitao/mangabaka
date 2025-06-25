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
                message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(value = "O nome do mangá não pode ser vázio."),
                errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA,
                httpError = Response.Status.BAD_REQUEST
            )
        }

        return try {
            val mangaAssetData: MangaPaginatedAssetsDto =
                query.queryAssetDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)

            if (mangaAssetData.page.media.isEmpty()) {
                throw MetadataException(
                    message = MetadataErrorCode.ERROR_FIELD_EMPTY.handle(value = "Nenhuma media foi encontrada para o manga: $mangaName"),
                    errorCode = MetadataErrorCode.ERROR_FIELD_EMPTY,
                    httpError = Response.Status.NOT_FOUND
                )
            }

            val maxAssets = PER_PAGE * 2
            val assetListUrl: Array<AssetInfo?> = arrayOfNulls(size = maxAssets)

            var index = 0
            for (value in mangaAssetData.page.media) {
                val mangaName = listOf(
                    value.title.english, value.title.romaji, value.title.native
                ).firstOrNull { !it.isNullOrBlank() } ?: throw MetadataException(
                    message = MetadataErrorCode.ERROR_FIELD_EMPTY.handle(value = "Não foi encontrado nome de manga para buscar os assets."),
                    errorCode = MetadataErrorCode.ERROR_FIELD_EMPTY,
                    httpError = Response.Status.BAD_GATEWAY
                )

                val coverUrl = value.coverImage.large?.takeIf { it.isNotBlank() } ?: throw MetadataException(
                    message = MetadataErrorCode.ERROR_FIELD_EMPTY.handle(value = "Não foi encontrado nenhum cover."),
                    errorCode = MetadataErrorCode.ERROR_FIELD_EMPTY,
                    httpError = Response.Status.BAD_GATEWAY
                )

                val bannerUrl = value.bannerImage?.takeIf { it.isNotBlank() } ?: throw MetadataException(
                    message = MetadataErrorCode.ERROR_FIELD_EMPTY.handle(value = "Não foi encontrado nenhum banner."),
                    errorCode = MetadataErrorCode.ERROR_FIELD_EMPTY,
                    httpError = Response.Status.BAD_GATEWAY
                )

                if (index < maxAssets) {
                    assetListUrl[index] = AssetInfo(url = bannerUrl, mangaName = mangaName, type = AssetType.BANNER)
                    index++
                }

                if (index < maxAssets) {
                    assetListUrl[index] = AssetInfo(url = coverUrl, mangaName = mangaName, type = AssetType.COVER)
                    index++
                }
            }

            val assetData = assetListUrl.filterNotNull().map { assetInfo ->
                val assetDownload = anilistMangaAssetClientDownload.fetchAsset(
                    endpoint = assetInfo.url, mangaName = assetInfo.mangaName, assetType = assetInfo.type
                )

                if (assetDownload.filename.isBlank() || assetDownload.filename.isEmpty()) throw AssetDownloadException(
                    message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(value = "Campo de nome ausente: ${assetDownload.filename}"),
                    errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA,
                    httpError = Response.Status.BAD_GATEWAY
                )

                if (assetDownload.mediaType.isBlank() || assetDownload.mediaType.isEmpty()) throw AssetDownloadException(
                    message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(value = "Campo de nome mediaType ausente: ${assetDownload.filename}"),
                    errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA,
                    httpError = Response.Status.BAD_GATEWAY
                )

                if (assetDownload.content.isEmpty()) throw AssetDownloadException(
                    message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(value = "Campo de nome content vázio: ${assetDownload.filename}"),
                    errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA,
                    httpError = Response.Status.BAD_GATEWAY
                )

                assetDownload
            }.toMutableList()

            MangaDataDto(null, assetData)
        } catch (exception: SerializationException) {
            throw MetadataException(
                message = MetadataErrorCode.ERROR_JSON_MALFORMED.handle(value = "Nenhuma media foi encontrada para o manga: ${exception.message}"),
                errorCode = MetadataErrorCode.ERROR_JSON_MALFORMED,
                httpError = Response.Status.BAD_REQUEST
            )
        } finally {
            anilistMangaAssetClientDownload.close();
        }
    }
}