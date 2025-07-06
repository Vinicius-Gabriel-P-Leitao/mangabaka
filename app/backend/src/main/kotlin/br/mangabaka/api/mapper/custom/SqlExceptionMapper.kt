/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.api.mapper.custom;

import br.mangabaka.api.mapper.response.BadGatewayResponse
import br.mangabaka.api.mapper.response.ConflictResponse
import br.mangabaka.api.mapper.response.ForbiddenResponse
import br.mangabaka.api.mapper.response.InternalServerErrorResponse
import br.mangabaka.api.mapper.response.MapperResponseResolver
import br.mangabaka.api.mapper.response.ServiceUnavailableResponse
import br.mangabaka.exception.code.custom.SqlErrorCode
import br.mangabaka.exception.throwable.http.SqlException
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
class SqlExceptionMapper : ExceptionMapper<SqlException> {
    companion object {
        private val logger: Logger = LogManager.getLogger(SqlExceptionMapper::class.java)
    }

    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: SqlException): Response {
        val uri = request.requestURI
        logger.error(
            I18n.get("throw.unexpected.error", "SqlExceptionMapper: ${exception.message}"),
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
                when (exception.errorCode as SqlErrorCode to exception.httpError) {
                    SqlErrorCode.ERROR_DUPLICATE_CONSTRAINT to Response.Status.BAD_GATEWAY,
                    SqlErrorCode.ERROR_NOT_NULL_VIOLATION to Response.Status.BAD_GATEWAY,
                    SqlErrorCode.ERROR_DATA_TOO_LONG to Response.Status.BAD_GATEWAY,
                    SqlErrorCode.ERROR_SYNTAX to Response.Status.BAD_GATEWAY,
                    SqlErrorCode.ERROR_UNDEFINED_COLUMN to Response.Status.BAD_GATEWAY -> {
                        MapperResponseResolver(
                            response = BadGatewayResponse(),
                            uri = uri,
                            message = exception.message
                        ).resolve()
                    }

                    SqlErrorCode.ERROR_FOREIGN_KEY_CONSTRAINT to Response.Status.CONFLICT -> {
                        MapperResponseResolver(
                            response = ConflictResponse(),
                            uri = uri,
                            message = exception.message
                        ).resolve()
                    }

                    SqlErrorCode.ERROR_PERMISSION_DENIED to Response.Status.FORBIDDEN -> {
                        MapperResponseResolver(
                            response = ForbiddenResponse(),
                            uri = uri,
                            message = exception.message
                        ).resolve()
                    }

                    SqlErrorCode.ERROR_CONNECTION_FAILURE to Response.Status.SERVICE_UNAVAILABLE -> {
                        MapperResponseResolver(
                            response = ServiceUnavailableResponse(),
                            uri = uri,
                            message = exception.message
                        ).resolve()
                    }

                    SqlErrorCode.ERROR_PERSIST_DATA to Response.Status.INTERNAL_SERVER_ERROR -> {
                        MapperResponseResolver(
                            response = InternalServerErrorResponse(),
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