/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.controller

import br.mangabaka.api.dto.MangaMetadata
import br.mangabaka.exception.code.http.InternalErrorCode
import br.mangabaka.exception.code.http.InvalidParameterErrorCode
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.InvalidParameterException
import br.mangabaka.service.external.anilist.FetchAnilistMangaDataService
import br.mangabaka.service.internal.MangaComicinfoService
import br.mangabaka.service.internal.MangaResolverService
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/manga")
class PostMangaComicinfo {
    @POST
    @Path("/comicinfo")
    @Produces(MediaType.APPLICATION_JSON)
    fun postComicInfo(nameManga: String?): Response {
        try {
            if (nameManga == null) {
                throw InvalidParameterException(
                    message = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY.handle(value = "O parâmetro de consulta 'nome' é obrigatório."),
                    errorCode = InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY, httpError = Response.Status.BAD_REQUEST
                )
            }

            val comicInfo: Response.Status = MangaComicinfoService().createComicinfo(nameManga)

            return Response
                .ok("ok")
                .build()
        } catch (exception: Exception) {
            throw InternalException(
                message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(value = "Erro ao buscar métadados: ${exception.message}"),
                errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
            )
        }
    }
}