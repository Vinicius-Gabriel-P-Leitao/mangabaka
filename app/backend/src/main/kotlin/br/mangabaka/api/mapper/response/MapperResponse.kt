/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper.response;

import jakarta.ws.rs.core.Response

interface MapperResponse {
    fun mapper(uri: String, message: String?): Response

    fun html(redirectUrl: String): String {
        return """
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
    }
}