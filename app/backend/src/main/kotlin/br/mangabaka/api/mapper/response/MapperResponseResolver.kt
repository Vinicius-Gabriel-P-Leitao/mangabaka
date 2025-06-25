/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.api.mapper.response;

import br.mangabaka.infrastructure.config.singleton.I18n
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URLEncoder
import kotlin.text.Charsets.UTF_8

class MapperResponseResolver(
    private val response: MapperResponse,
    private val uri: String,
    private val message: String?,
    private val validPrefix: String = "/v1"
) {
    fun resolve(): Response {
        if (!uri.startsWith(validPrefix)) return invalidUriResponse()

        val encodedUri = URLEncoder.encode(uri, UTF_8)
        val reason = URLEncoder.encode(message, UTF_8)

        return response.mapper(uri = encodedUri, message = reason)
    }

    private fun invalidUriResponse(): Response {
        return Response.status(Response.Status.NOT_FOUND)
            .entity("""{"error":${I18n.get("throw.error.redirect.frontend")}}""")
            .type(MediaType.APPLICATION_JSON)
            .build()
    }
}