/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.controller

import br.mangabaka.api.dto.AssetType
import br.mangabaka.api.dto.MangaDataDto
import br.mangabaka.exception.code.http.AssetDownloadErrorCode
import br.mangabaka.exception.code.http.InternalErrorCode
import br.mangabaka.exception.code.http.InvalidParameterErrorCode
import br.mangabaka.exception.throwable.base.AppException
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.exception.throwable.http.InvalidParameterException
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.infrastructure.http.anilist.dto.DownloadedAssetDto
import br.mangabaka.service.external.anilist.FetchAnilistMangaAssetService
import br.mangabaka.service.external.anilist.FetchAnilistMangaDataService
import br.mangabaka.service.internal.MangaResolverService
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Path("/manga")
class GetDataManga {
    companion object {
        private val logger: Logger = LogManager.getLogger(GetDataManga::class.java)
    }

    @GET
    @Path("/metadata")
    @Produces(MediaType.APPLICATION_JSON)
    fun getMetadataManga(@QueryParam("name") nameManga: String?): Response {
        try {
            if (nameManga == null) {
                throw InvalidParameterException(
                    message = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY.handle(value = "O parâmetro de consulta 'nome' é obrigatório."),
                    errorCode = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY, httpError = Response.Status.BAD_REQUEST
                )
            }

            val fetchAnilistMangaDataService = FetchAnilistMangaDataService()
            val resolverService = MangaResolverService(services = fetchAnilistMangaDataService)

            val result: MangaDataDto = resolverService.mangaResolver(mangaName = nameManga)
            if (result.paginationInfo == null) {
                throw MetadataException(
                    message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(value = "Os metádados estão vázios."),
                    errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA, httpError = Response.Status.NOT_FOUND
                )
            }

            return Response
                .ok(result.paginationInfo.page)
                .build()
        } catch (exception: Exception) {
            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(value = "Erro ao buscar métadados: ${exception.message}"),
                    errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }

    @GET
    @Path("/assets")
    @Produces(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON)
    fun getAssetsManga(@QueryParam("name") nameManga: String?, @QueryParam("type") typeParam: String?): Response {
        try {
            if (nameManga == null || typeParam == null) {
                throw InvalidParameterException(
                    message = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY.handle(value = "Os parâmetros de consulta 'name' e 'type' são obrigatórios."),
                    errorCode = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY, httpError = Response.Status.BAD_REQUEST
                )
            }

            val assetType = try {
                AssetType.valueOf(typeParam.uppercase())
            } catch (exception: Exception) {
                throw InvalidParameterException(
                    message = InvalidParameterErrorCode.ERROR_PARAMETER_INVALID.handle(value = "Tipo inválido. Valores permitidos: capa, banner: ${exception.message}"),
                    errorCode = InvalidParameterErrorCode.ERROR_PARAMETER_INVALID,
                    httpError = Response.Status.BAD_REQUEST
                )
            }

            val fetchAnilistMangaAssetService = FetchAnilistMangaAssetService()
            val resolverService = MangaResolverService(services = fetchAnilistMangaAssetService)

            val result: MangaDataDto = resolverService.mangaResolver(mangaName = nameManga)
            if (result.assets == null) {
                throw AssetDownloadException(
                    message = AssetDownloadErrorCode.ERROR_EMPTY_DATA.handle(value = "Os dados de assets estão vázios."),
                    errorCode = AssetDownloadErrorCode.ERROR_EMPTY_DATA, httpError = Response.Status.NOT_FOUND
                )
            }

            val asset = when (assetType) {
                AssetType.COVER -> result.assets.find { asset: DownloadedAssetDto -> asset.assetType == AssetType.COVER }
                AssetType.BANNER -> result.assets.find { asset: DownloadedAssetDto -> asset.assetType == AssetType.BANNER }
            } ?: throw InvalidParameterException(
                message = InvalidParameterErrorCode.ERROR_PARAMETER_INVALID.handle(value = "Ativo do tipo $typeParam não encontrado."),
                errorCode = InvalidParameterErrorCode.ERROR_PARAMETER_INVALID, httpError = Response.Status.NOT_FOUND
            )

            return Response
                .ok(asset.content)
                .header("Content-Disposition", "attachment; filename=\"${asset.filename}\"")
                .build()
        } catch (exception: Exception) {
            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(value = "Erro ao buscar assets: ${exception.message}"),
                    errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }
}