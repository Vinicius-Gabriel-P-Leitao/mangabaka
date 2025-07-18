/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.api.mapper.custom

import br.mangabaka.api.mapper.response.MapperResponseResolver
import br.mangabaka.api.mapper.response.BadGatewayResponse
import br.mangabaka.api.mapper.response.BadRequestResponse
import br.mangabaka.api.mapper.response.InternalServerErrorResponse
import br.mangabaka.api.mapper.response.NotFoundResponse
import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.infrastructure.config.singleton.AppConfig
import br.mangabaka.infrastructure.config.singleton.BackendMode
import br.mangabaka.infrastructure.config.singleton.I18n
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Provider
class MetadataExceptionMapper : ExceptionMapper<MetadataException> {
    companion object {
        private val logger: Logger = LogManager.getLogger(InternalExceptionMapper::class.java)
    }

    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: MetadataException): Response {
        val uri = request.requestURI
        logger.error(
            I18n.get("throw.unexpected.error", "MetadataExceptionMapper: ${exception.message}"),
            exception
        )


        return when (AppConfig.backendMode) {
            BackendMode.API -> {
                Response.status(exception.httpError).entity(
                    mapOf(
                        "error" to exception.message, "code" to exception.errorCode, "cause" to exception.cause?.message
                    )
                ).type(MediaType.APPLICATION_JSON).build()
            }

            BackendMode.ALL -> {
                when (exception.errorCode as MetadataErrorCode to exception.httpError) {
                    MetadataErrorCode.ERROR_EMPTY_FIELD to Response.Status.BAD_GATEWAY -> {
                        MapperResponseResolver(
                            response = BadGatewayResponse(),
                            uri = uri,
                            message = exception.message
                        ).resolve()
                    }

                    MetadataErrorCode.ERROR_EMPTY_FIELD to Response.Status.NOT_FOUND -> {
                        MapperResponseResolver(
                            response = NotFoundResponse(),
                            uri = uri,
                            message = exception.message
                        ).resolve()
                    }

                    MetadataErrorCode.ERROR_JSON_MALFORMED to Response.Status.BAD_REQUEST -> {
                        MapperResponseResolver(
                            response = BadRequestResponse(),
                            uri = uri,
                            message = exception.message
                        ).resolve()
                    }

                    else -> {
                        MapperResponseResolver(
                            response = InternalServerErrorResponse(),
                            uri = uri,
                            message = exception.message
                        ).resolve()
                    }
                }
            }
        }
    }
}