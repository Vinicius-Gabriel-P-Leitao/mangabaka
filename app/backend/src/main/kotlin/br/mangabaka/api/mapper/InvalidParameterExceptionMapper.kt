/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper

import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.InvalidParameterException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper

class InvalidParameterExceptionMapper : ExceptionMapper<InvalidParameterException> {
    override fun toResponse(exception: InvalidParameterException): Response {
        return Response.status(exception.httpError)
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