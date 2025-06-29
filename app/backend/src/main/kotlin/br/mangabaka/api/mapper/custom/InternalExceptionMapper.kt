/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper.custom

import br.mangabaka.api.mapper.response.MapperResponseResolver
import br.mangabaka.api.mapper.response.InternalServerErrorResponse
import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.infrastructure.config.singleton.AppConfig
import br.mangabaka.infrastructure.config.singleton.BackendMode
import br.mangabaka.infrastructure.config.singleton.I18n
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Provider
class InternalExceptionMapper : ExceptionMapper<InternalException> {
    companion object {
        private val logger: Logger = LogManager.getLogger(InternalExceptionMapper::class.java)
    }

    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: InternalException): Response {
        val uri = request.requestURI
        logger.error(
            I18n.get("throw.unexpected.error", "InternalExceptionMapper: ${exception.message}"),
            exception
        )

        return when (AppConfig.backendMode) {
            BackendMode.API -> {
                Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(
                        mapOf(
                            "error" to exception.message,
                            "code" to exception.errorCode,
                            "cause" to exception.cause?.message
                        )
                    )
                    .build()
            }

            BackendMode.ALL -> {
                when (exception.errorCode as InternalErrorCode) {
                    InternalErrorCode.ERROR_INTERNAL_GENERIC, InternalErrorCode.ERROR_TRANSLATE -> {
                        MapperResponseResolver(
                            response = InternalServerErrorResponse(), uri = uri, message = exception.message
                        ).resolve()
                    }
                }
            }
        }
    }
}