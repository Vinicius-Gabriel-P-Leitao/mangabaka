/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.mapper.response

import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

open class ErrorResponse(private val status: Response.Status, private val endpoint: String) : MapperResponse {
    override fun mapper(uri: String, message: String?): Response {
        val redirectUrl = "$endpoint?original=$uri&reason=$message"
        return Response.status(status).entity(html(redirectUrl)).type(MediaType.TEXT_HTML).build()
    }
}

class GatewayTimeoutResponse : ErrorResponse(Response.Status.GATEWAY_TIMEOUT, "/api-gateway-timeout")
class BadRequestResponse : ErrorResponse(Response.Status.BAD_REQUEST, "/api-bad-request")
class BadGatewayResponse : ErrorResponse(Response.Status.BAD_GATEWAY, "/api-bad-gateway")
class InternalServerErrorResponse : ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "/api-internal-error")
class NotFoundResponse : ErrorResponse(Response.Status.NOT_FOUND, "/api-not-found")
