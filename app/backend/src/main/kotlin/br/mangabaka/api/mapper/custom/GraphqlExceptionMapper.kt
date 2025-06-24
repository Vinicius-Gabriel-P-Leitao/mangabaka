/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper.custom

import br.mangabaka.api.mapper.response.*
import br.mangabaka.exception.code.custom.GraphqlErrorCode
import br.mangabaka.exception.throwable.http.GraphqlException
import br.mangabaka.infrastructure.config.AppConfig
import br.mangabaka.infrastructure.config.BackendMode
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Provider
class GraphqlExceptionMapper : ExceptionMapper<GraphqlException> {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GraphqlExceptionMapper::class.java)
    }

    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: GraphqlException): Response {
        val uri = request.requestURI
        logger.error("Erro inesperado na GraphQLException: ${exception.message}", exception)

        return when (AppConfig.backendMode) {
            BackendMode.API -> {
                Response.status(exception.httpError).entity(
                    mapOf(
                        "error" to exception.message, "code" to exception.errorCode, "cause" to exception.cause?.message
                    )
                ).type(MediaType.APPLICATION_JSON).build()
            }

            BackendMode.ALL, BackendMode.CUSTOM -> {
                when (exception.errorCode as GraphqlErrorCode to exception.httpError) {
                    GraphqlErrorCode.ERROR_CLIENT to Response.Status.BAD_GATEWAY,
                    GraphqlErrorCode.ERROR_EMPTY_RESPONSE to Response.Status.BAD_GATEWAY -> {
                        MapperResponseResolver(
                            response = BadGatewayResponse(), uri = uri, message = exception.message
                        ).resolve()
                    }

                    GraphqlErrorCode.ERROR_INVALID_URL to Response.Status.BAD_REQUEST -> {
                        MapperResponseResolver(
                            response = BadRequestResponse(), uri = uri, message = exception.message
                        ).resolve()
                    }

                    GraphqlErrorCode.ERROR_TIMEOUT to Response.Status.GATEWAY_TIMEOUT -> {
                        MapperResponseResolver(
                            response = GatewayTimeoutResponse(), uri = uri, message = exception.message
                        ).resolve()
                    }

                    else -> {
                        MapperResponseResolver(
                            response = InternalServerErrorResponse(), uri = uri, message = exception.message
                        ).resolve()
                    }
                }
            }
        }
    }
}