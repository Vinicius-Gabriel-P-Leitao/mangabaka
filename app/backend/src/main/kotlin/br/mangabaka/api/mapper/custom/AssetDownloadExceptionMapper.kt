/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper.custom

import br.mangabaka.api.mapper.response.*
import br.mangabaka.exception.code.custom.AssetDownloadErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
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
class AssetDownloadExceptionMapper : ExceptionMapper<AssetDownloadException> {
    companion object {
        private val logger: Logger = LogManager.getLogger(InternalExceptionMapper::class.java)
    }

    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: AssetDownloadException): Response {
        val uri = request.requestURI
        logger.error(
            I18n.get("throw.unexpected.error", "AssetDownloadExceptionMapper: ${exception.message}"),
            exception
        )

        return when (AppConfig.backendMode) {
            BackendMode.API -> {
                Response.status(exception.httpError).entity(
                    mapOf(
                        "error" to exception.message, "code" to exception.errorCode
                    )
                ).type(MediaType.APPLICATION_JSON).build()
            }

            BackendMode.ALL -> {
                when (exception.errorCode as AssetDownloadErrorCode to exception.httpError) {
                    AssetDownloadErrorCode.ERROR_CLIENT_STATUS to Response.Status.BAD_GATEWAY,
                    AssetDownloadErrorCode.ERROR_INVALID_URL to Response.Status.BAD_GATEWAY,
                    AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION to Response.Status.BAD_GATEWAY,
                    AssetDownloadErrorCode.ERROR_EMPTY_DATA to Response.Status.BAD_GATEWAY -> {
                        MapperResponseResolver(
                            response = BadGatewayResponse(), uri = uri, message = exception.message
                        ).resolve()
                    }

                    AssetDownloadErrorCode.ERROR_EMPTY_DATA to Response.Status.BAD_REQUEST -> {
                        MapperResponseResolver(
                            response = BadRequestResponse(), uri = uri, message = exception.message
                        ).resolve()
                    }

                    AssetDownloadErrorCode.ERROR_EMPTY_DATA to Response.Status.NOT_FOUND -> {
                        MapperResponseResolver(
                            response = NotFoundResponse(), uri = uri, message = exception.message
                        ).resolve()
                    }

                    AssetDownloadErrorCode.ERROR_TIMEOUT to Response.Status.GATEWAY_TIMEOUT -> {
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