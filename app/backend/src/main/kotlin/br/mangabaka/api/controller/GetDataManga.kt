/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.api.controller

import br.mangabaka.api.dto.AssetType
import br.mangabaka.api.dto.MangaDataDto
import br.mangabaka.exception.code.custom.AssetDownloadErrorCode
import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.code.custom.ParameterErrorCode
import br.mangabaka.exception.throwable.base.AppException
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.exception.throwable.http.InvalidParameterException
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.infrastructure.config.singleton.I18n
import br.mangabaka.infrastructure.http.anilist.dto.DownloadedAssetDto
import br.mangabaka.service.external.anilist.FetchAnilistMangaAssetService
import br.mangabaka.service.external.anilist.FetchAnilistMangaDataService
import br.mangabaka.service.internal.MangaResolverService
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path(value = "/manga")
class GetDataManga(
    private val fetchAnilistMangaDataService: FetchAnilistMangaDataService = FetchAnilistMangaDataService(),
    private val fetchAnilistMangaAssetService: FetchAnilistMangaAssetService = FetchAnilistMangaAssetService(),
) {
    @GET
    @Path(value = "/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAnilistMetadataManga(@QueryParam("name") nameManga: String?): Response {
        return try {
            if (nameManga == null || nameManga.isEmpty()) {
                throw InvalidParameterException(
                    message = ParameterErrorCode.ERROR_PARAMETER_EMPTY.handle(
                        value = I18n.get(
                            "throw.parameter.require", "name"
                        )
                    ), errorCode = ParameterErrorCode.ERROR_PARAMETER_EMPTY, httpError = Response.Status.BAD_REQUEST
                )
            }

            val resolverService = MangaResolverService(services = fetchAnilistMangaDataService)
            val result: MangaDataDto = resolverService.mangaResolver(mangaName = nameManga)
            if (result.paginationInfo == null) {
                throw MetadataException(
                    message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(value = I18n.get("throw.metadata.empty")),
                    errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA,
                    httpError = Response.Status.NOT_FOUND
                )
            }

            Response.ok(result.paginationInfo.page).build()
        } catch (exception: Exception) {
            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(
                        value = I18n.get(
                            key = "throw.error.fetch.metadata", I18n.get("throw.unknown.error")
                        )
                    ), errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }

    @GET
    @Path(value = "/asset")
    @Produces(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON)
    fun getAnilistAssetManga(@QueryParam("name") nameManga: String?, @QueryParam("type") typeParam: String?): Response {
        return try {
            if (nameManga == null || nameManga.isEmpty() || typeParam == null || typeParam.isEmpty()) {
                throw InvalidParameterException(
                    message = ParameterErrorCode.ERROR_PARAMETER_EMPTY.handle(
                        value = I18n.get(
                            key = "throw.parameter.require", "name, type"
                        )
                    ), errorCode = ParameterErrorCode.ERROR_PARAMETER_EMPTY, httpError = Response.Status.BAD_REQUEST
                )
            }

            val assetType = try {
                AssetType.valueOf(typeParam.uppercase())
            } catch (exception: Exception) {
                throw InvalidParameterException(
                    message = ParameterErrorCode.ERROR_PARAMETER_INVALID.handle(value = I18n.get("anilist.asset.invalid.type")),
                    errorCode = ParameterErrorCode.ERROR_PARAMETER_INVALID,
                    httpError = Response.Status.BAD_REQUEST
                )
            }

            val resolverService = MangaResolverService(services = fetchAnilistMangaAssetService)
            val result: MangaDataDto = resolverService.mangaResolver(mangaName = nameManga)
            if (result.asset == null) {
                throw AssetDownloadException(
                    message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(value = I18n.get("throw.empty.asset.data")),
                    errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA,
                    httpError = Response.Status.NOT_FOUND
                )
            }

            val asset = when (assetType) {
                AssetType.COVER -> result.asset.find { asset: DownloadedAssetDto -> asset.assetType == AssetType.COVER }
                AssetType.BANNER -> result.asset.find { asset: DownloadedAssetDto -> asset.assetType == AssetType.BANNER }
            } ?: throw InvalidParameterException(
                message = ParameterErrorCode.ERROR_PARAMETER_INVALID.handle(
                    value = I18n.get(
                        key = "throw.not.found.asset.type", typeParam
                    )
                ), errorCode = ParameterErrorCode.ERROR_PARAMETER_INVALID, httpError = Response.Status.BAD_REQUEST
            )

            Response.ok(asset.content)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${asset.filename}\"").build()
        } catch (exception: Exception) {
            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(
                        value = I18n.get(
                            key = "throw.error.fetch.asset", I18n.get("throw.unknown.error")
                        )
                    ), errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }
}