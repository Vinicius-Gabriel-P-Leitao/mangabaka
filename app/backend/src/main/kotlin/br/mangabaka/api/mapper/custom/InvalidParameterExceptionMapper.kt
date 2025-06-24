/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper.custom

import br.mangabaka.exception.throwable.http.InvalidParameterException
import br.mangabaka.infrastructure.config.AppConfig
import br.mangabaka.infrastructure.config.BackendMode
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

// @formatter:off
@Provider
class InvalidParameterExceptionMapper : ExceptionMapper<InvalidParameterException> {
    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: InvalidParameterException): Response {
        val uri = request.requestURI

        return when (AppConfig.backendMode) {
            BackendMode.API -> {
                Response.status(exception.httpError).entity(
                    mapOf(
                        "error" to exception.message,
                        "code" to exception.errorCode,
                        "cause" to exception.cause?.message
                    )
                ).type(MediaType.APPLICATION_JSON).build()
            }

            BackendMode.ALL, BackendMode.CUSTOM -> {
                if (uri.startsWith("/v1")) {
                    val encodedUri = URLEncoder.encode(uri, UTF_8)
                    val reason = URLEncoder.encode(exception.message, UTF_8)

                    val redirectUrl = "/api-bad-request?original=$encodedUri&reason=$reason"

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
                    Response.status(Response.Status.NOT_FOUND).entity("Erro ao redirecionar para o frontend").type(MediaType.APPLICATION_JSON)
                        .build()
                }
            }
        }
    }
}