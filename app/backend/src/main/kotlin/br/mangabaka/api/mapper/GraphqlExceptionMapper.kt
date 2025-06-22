/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper

import br.mangabaka.exception.throwable.http.GraphqlException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class GraphqlExceptionMapper : ExceptionMapper<GraphqlException> {
    override fun toResponse(exception: GraphqlException): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(
                mapOf(
                    "error" to exception.message,
                    "code" to exception.errorCode,
                    "cause" to exception.cause?.message
                )
            )
            .build()
    }
}