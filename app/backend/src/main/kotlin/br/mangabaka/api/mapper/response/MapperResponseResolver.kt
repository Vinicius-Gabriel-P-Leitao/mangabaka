/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.api.mapper.response;

import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

class MapperResponseResolver(
    private val response: MapperResponse,
    private val uri: String,
    private val message: String?
) {
    fun resolve(): Response {
        return if (uri.startsWith(prefix = "/v1")) {
            val encodedUri = URLEncoder.encode(uri, UTF_8)
            val reason = URLEncoder.encode(message, UTF_8)

            response.mapper(uri = encodedUri, message = reason)
        } else {
            Response.status(Response.Status.NOT_FOUND).entity("Erro ao redirecionar para o frontend.")
                .type(MediaType.APPLICATION_JSON)
                .build()
        }
    }
}