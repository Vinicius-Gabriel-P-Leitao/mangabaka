/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.api.mapper.custom

import br.mangabaka.exception.throwable.http.MetadataException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class MetadataExceptionMapper : ExceptionMapper<MetadataException> {
    override fun toResponse(exception: MetadataException): Response {
        return Response.status(exception.httpError)
            .entity(
                mapOf(
                    "error" to exception.message,
                    "code" to exception.errorCode,
                )
            )
            .type(MediaType.APPLICATION_JSON)
            .build()
    }
}