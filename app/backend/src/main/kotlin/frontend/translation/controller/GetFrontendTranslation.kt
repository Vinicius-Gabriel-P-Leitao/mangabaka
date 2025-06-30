/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.controller;

import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.code.custom.ParameterErrorCode
import br.mangabaka.exception.throwable.base.AppException
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.InvalidParameterException
import br.mangabaka.infrastructure.config.singleton.I18n
import frontend.translation.dto.AvailableTranslationsDto
import frontend.translation.dto.I18nJsonFormat
import frontend.translation.repository.FrontendTranslationRepo
import frontend.translation.service.FetchFrontendTranslationService
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Path(value = "/frontend/translation")
class GetFrontendTranslation(
    private val repository: FrontendTranslationRepo = FrontendTranslationRepo(),
    private val service: FetchFrontendTranslationService =
        FetchFrontendTranslationService(repository),
) {
    companion object {
        private val logger: Logger = LogManager.getLogger(GetFrontendTranslation::class.java)
    }

    @GET
    @Path(value = "/language")
    @Produces(MediaType.APPLICATION_JSON)
    fun getAvailableTranslations(): Response {
        return try {
            val response: List<AvailableTranslationsDto> = service.fetchAvailableTranslations()
            Response.ok(response).build()
        } catch (exception: Exception) {
            logger.error(exception)

            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(
                        value = I18n.get(
                            "throw.error.fetch.available.translation", I18n.get("throw.unknown.error")
                        )
                    ), errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getTranslation(@QueryParam(value = "lang") lang: String?): Response {
        return try {
            if (lang == null || lang.isEmpty()) {
                throw InvalidParameterException(
                    message = ParameterErrorCode.ERROR_PARAMETER_EMPTY.handle(
                        value = I18n.get(
                            "throw.parameter.require",
                            "lang"
                        )
                    ),
                    errorCode = ParameterErrorCode.ERROR_PARAMETER_EMPTY,
                    httpError = Response.Status.BAD_REQUEST
                )
            }

            val response: I18nJsonFormat = service.fetchDataTranslation(lang)
            Response.ok(response).build()
        } catch (exception: Exception) {
            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(
                        value = I18n.get(
                            "throw.error.fetch.translation", I18n.get("throw.unknown.error")
                        )
                    ), errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }
}