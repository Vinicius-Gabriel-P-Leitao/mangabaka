/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.api.mapper.jersey;

import br.mangabaka.api.mapper.response.MapperResponse
import br.mangabaka.api.mapper.response.MapperResponseResolver
import br.mangabaka.api.mapper.response.BadRequestResponse
import br.mangabaka.infrastructure.config.AppConfig
import br.mangabaka.infrastructure.config.BackendMode
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class BadRequestExceptionMapper : ExceptionMapper<BadRequestException> {
    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: BadRequestException): Response {
        val uri = request.requestURI

        return when (AppConfig.backendMode) {
            BackendMode.API -> {
                val errorMap = mapOf("error" to (exception.message ?: "Bad Request"))

                Response.status(Response.Status.BAD_REQUEST).entity(
                    errorMap
                ).type(MediaType.APPLICATION_JSON).build()
            }

            BackendMode.ALL, BackendMode.CUSTOM -> {
                val mapperResponse: MapperResponse = BadRequestResponse()
                MapperResponseResolver(response = mapperResponse, uri = uri, message = exception.message).resolve()
            }
        }
    }
}