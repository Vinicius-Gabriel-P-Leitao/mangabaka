/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper.custom

import br.mangabaka.api.mapper.response.MapperResponseResolver
import br.mangabaka.api.mapper.response.BadRequestResponse
import br.mangabaka.exception.code.custom.InvalidParameterErrorCode
import br.mangabaka.exception.throwable.http.InvalidParameterException
import br.mangabaka.infrastructure.config.AppConfig
import br.mangabaka.infrastructure.config.BackendMode
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger


@Provider
class InvalidParameterExceptionMapper : ExceptionMapper<InvalidParameterException> {
    companion object {
        private val logger: Logger = LogManager.getLogger(InternalExceptionMapper::class.java)
    }

    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: InvalidParameterException): Response {
        val uri = request.requestURI
        logger.error("Erro inesperado na InvalidParameterExceptionMapper: ${exception.message}", exception)

        return when (AppConfig.backendMode) {
            BackendMode.API -> {
                Response.status(exception.httpError).entity(
                    mapOf(
                        "error" to exception.message, "code" to exception.errorCode, "cause" to exception.cause?.message
                    )
                ).type(MediaType.APPLICATION_JSON).build()
            }

            BackendMode.ALL -> {
                when (exception.errorCode as InvalidParameterErrorCode) {
                    InvalidParameterErrorCode.ERROR_PARAMETER_EMPTY, InvalidParameterErrorCode.ERROR_PARAMETER_INVALID -> {
                        MapperResponseResolver(
                            response = BadRequestResponse(), uri = uri, message = exception.message
                        ).resolve()
                    }
                }
            }
        }
    }
}