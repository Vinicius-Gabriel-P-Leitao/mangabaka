/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.api.mapper.jersey;

import br.mangabaka.api.mapper.response.MapperResponse
import br.mangabaka.api.mapper.response.MapperResponseResolver
import br.mangabaka.api.mapper.response.MethodNotAllowedResponse
import br.mangabaka.infrastructure.config.AppConfig
import br.mangabaka.infrastructure.config.BackendMode
import br.mangabaka.infrastructure.config.singleton.I18n
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.NotAllowedException
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
@Provider
class MethodNotAllowedMapper : ExceptionMapper<NotAllowedException> {
    companion object {
        private val logger: Logger = LogManager.getLogger(MethodNotAllowedMapper::class.java)
    }

    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: NotAllowedException): Response {
        val uri = request.requestURI
        logger.error(
            I18n.get("throw.unexpected.error", "MethodNotAllowedMapper: ${exception.message}"),
            exception
        )

        return when (AppConfig.backendMode) {
            BackendMode.API -> {
                val errorMap = mapOf("error" to (exception.message ?: "Method not Allowed"))

                Response.status(Response.Status.METHOD_NOT_ALLOWED).entity(
                    errorMap
                ).type(MediaType.APPLICATION_JSON).build()
            }

            BackendMode.ALL -> {
                val mapperResponse: MapperResponse = MethodNotAllowedResponse()
                MapperResponseResolver(response = mapperResponse, uri = uri, message = exception.message).resolve()
            }
        }
    }
}