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
    // TODO: Implementar toda lógica desse método
    @POST
    @Path("/comicinfo")
    @Produces(MediaType.APPLICATION_JSON)
    fun postComicInfo(data: String?): Response {
        return try {
            if (data == null) {
                throw InvalidParameterException(
                    message = ParameterErrorCode.ERROR_BODY_EMPTY.handle(value = I18n.get("throw.json.data.body.require")),
                    errorCode = ParameterErrorCode.ERROR_BODY_EMPTY, httpError = Response.Status.BAD_REQUEST
                )
            }

            val comicInfo: Response.Status = MangaComicinfoService().createComicinfo(data)

            Response.ok("ok").build()
        } catch (exception: Exception) {
            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(
                        value = I18n.get(
                            "throw.error.fetch.metadata", I18n.get("throw.unknown.error")
                        )
                    ), errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }
}