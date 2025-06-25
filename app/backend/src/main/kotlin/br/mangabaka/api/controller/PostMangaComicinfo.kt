/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.controller

import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.code.custom.ParameterErrorCode
import br.mangabaka.exception.throwable.base.AppException
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.InvalidParameterException
import br.mangabaka.infrastructure.config.singleton.I18n
import br.mangabaka.service.internal.MangaComicinfoService
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
        return try {
            if (nameManga == null) {
                throw InvalidParameterException(
                    message = ParameterErrorCode.ERROR_PARAMETER_EMPTY.handle(value = I18n.get("throw.name.parameter.require")),
                    errorCode = ParameterErrorCode.ERROR_PARAMETER_EMPTY, httpError = Response.Status.BAD_REQUEST
                )
            }

            val comicInfo: Response.Status = MangaComicinfoService().createComicinfo(nameManga)

            Response.ok("ok").build()
        } catch (exception: Exception) {
            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(value = "Erro ao buscar Comicinfo: ${exception.message}"),
                    errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }
}