/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.api.mapper.response.redirect;

import br.mangabaka.api.mapper.response.MapperResponse
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

class BadGatewayResponse : MapperResponse {
    override fun mapper(uri: String, message: String?): Response {
        val redirectUrl = "/api-bad-gateway?original=$uri&reason=$message"

        return Response.status(Response.Status.OK).entity(
            """
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
        ).type(MediaType.TEXT_HTML).build()
    }
}