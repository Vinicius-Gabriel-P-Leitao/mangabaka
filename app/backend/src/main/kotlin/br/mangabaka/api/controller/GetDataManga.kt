package br.mangabaka.api.controller

import br.mangabaka.api.dto.AssetType
import br.mangabaka.api.dto.MangaMetadata
import br.mangabaka.exception.code.http.InternalErrorCode
import br.mangabaka.exception.code.http.InvalidParameterErrorCode
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.InvalidParameterException
import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
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
    fun getMetadataManga(@QueryParam("name") name: String?): Response {
        try {
            if (name == null) {
                throw InvalidParameterException(
                    message = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY.handle(value = "O parâmetro de consulta 'nome' é obrigatório."),
                    errorCode = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY, httpError = Response.Status.BAD_REQUEST
                )
            }

            val fetchAnilistMangaDataService = FetchAnilistMangaDataService()
            val resolverService = MangaResolverService(listOf(fetchAnilistMangaDataService))

            val result: MangaMetadata = resolverService.mangaResolver(name)
            return Response
                .ok(result.paginationInfo.page)
                .build()
        } catch (exception: Exception) {
            throw InternalException(
                message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(value = "Erro ao buscar métadados: ${exception.message}"),
                errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
            )
        }
    }

    @GET
    @Path("/assets")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun getAssetsManga(@QueryParam("name") name: String?, @QueryParam("type") typeParam: String?): Response {
        try {
            if (name == null || typeParam == null) {
                throw InvalidParameterException(
                    message = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY.handle(value = "Os parâmetros de consulta 'nome' e 'tipo' são obrigatórios."),
                    errorCode = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY, httpError = Response.Status.BAD_REQUEST
                )
            }

            val assetType = try {
                AssetType.valueOf(typeParam.uppercase())
            } catch (exception: IllegalArgumentException) {
                throw InvalidParameterException(
                    message = InvalidParameterErrorCode.ERROR_PARAMETER_INVALID.handle(value = "Tipo inválido. Valores permitidos: capa, banner: ${exception.message}"),
                    errorCode = InvalidParameterErrorCode.ERROR_PARAMETER_INVALID,
                    httpError = Response.Status.BAD_REQUEST
                )
            }

            val fetchAnilistMangaDataService = FetchAnilistMangaDataService()
            val resolverService = MangaResolverService(listOf(fetchAnilistMangaDataService))

            val result: MangaMetadata = resolverService.mangaResolver(name)

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
            throw InternalException(
                message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(value = "Erro ao buscar assets: ${exception.message}"),
                errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
            )
        }
    }
}