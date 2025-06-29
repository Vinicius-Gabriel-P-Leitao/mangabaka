/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.controller

import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.code.custom.ParameterErrorCode
import br.mangabaka.exception.throwable.base.AppException
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.InvalidParameterException
import br.mangabaka.infrastructure.config.singleton.I18n
import frontend.translation.repository.FrontendTranslationRepo
import frontend.translation.service.FrontendTranslationService
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path(value = "/frontend/translation")
class PostFrontendTranslation(
    private val repository: FrontendTranslationRepo = FrontendTranslationRepo(),
    private val service: FrontendTranslationService = FrontendTranslationService(repository)
) {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createTranslation(data: String?): Response {
        return try {
            if (data.isNullOrEmpty()) {
                throw InvalidParameterException(
                    message = ParameterErrorCode.ERROR_BODY_EMPTY.handle(value = I18n.get("throw.json.data.body.require")),
                    errorCode = ParameterErrorCode.ERROR_BODY_EMPTY,
                    httpError = Response.Status.BAD_REQUEST
                )
            }

            service.saveTranslation(data = data)
            Response.status(Response.Status.CREATED).entity("Salvo com sucesso!").build()
        } catch (exception: Exception) {
            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(
                        value = I18n.get(
                            "throw.error.fetch.metadata", exception.message ?: I18n.get("throw.unknown.error")
                        )
                    ), errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }
}