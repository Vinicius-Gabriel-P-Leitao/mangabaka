/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper.jersey

import br.mangabaka.infrastructure.config.AppConfig
import br.mangabaka.infrastructure.config.BackendMode
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import java.net.URLEncoder

// @formatter:off
@Provider
class NotFoundExceptionMapper : ExceptionMapper<NotFoundException> {
    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: NotFoundException): Response {
        val uri = request.requestURI

        return when (AppConfig.backendMode) {
            BackendMode.API -> {
                val errorMap = mapOf("error" to (exception.message ?: "404 Not Found"))

                Response.status(Response.Status.NOT_FOUND).entity(
                    errorMap
                ).type(MediaType.APPLICATION_JSON).build()
            }

            BackendMode.ALL, BackendMode.CUSTOM -> {
                if (uri.startsWith("/v1")) {
                    val encodedUri = URLEncoder.encode(uri, Charsets.UTF_8)
                    val reason = URLEncoder.encode(exception.message, Charsets.UTF_8)

                    val redirectUrl = "/api-not-found?original=$encodedUri&reason=$reason"

                    val htmlRedirect = """
                                    <!DOCTYPE html>
                                    <html>
                                      <head>
                                        <meta charset="UTF-8">
                                        <meta http-equiv="refresh" content="0;URL='$redirectUrl'" />
                                        <script>
                                            window.location.href = '$redirectUrl';
                                        </script>
                                      </head>
                                      <body>
                                        Redirecionando...
                                      </body>
                                    </html>
                                       """.trimIndent()

                    Response.status(Response.Status.OK).entity(htmlRedirect).type(MediaType.TEXT_HTML)
                        .build()
                } else {
                    Response.status(Response.Status.NOT_FOUND).entity("404 Not Found").type(MediaType.APPLICATION_JSON)
                        .build()
                }
            }
        }
    }
}