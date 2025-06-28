/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package frontend.translation.dto

import kotlinx.serialization.SerialName

data class I18nJsonFormat(
    val meta: Meta,
    val page: Page,
    val component: Component,
    val handler: Handler
)

data class Meta(
    val language: String
)

data class Page(
    @SerialName("not_found") val notFound: ErrorPage,
    @SerialName("internal_server_error") val internalServerError: ErrorPage,
    @SerialName("gateway_timeout") val gatewayTimeout: ErrorPage,
    @SerialName("bad_request") val badRequest: ErrorPage,
    @SerialName("bad_gateway") val badGateway: ErrorPage,
    val home: HomePage
)

data class ErrorPage(
    val title: String,
    val message: String,
    val cause: String,
    val imageAlt: String
)

data class HomePage(
    val title: String
)

data class Component(
    val translation: Translation
)

data class Translation(
    val infoView: String
)

data class Handler(
    val unknown: UnknownHandler,
    val notFound: NotFoundHandler,
    val badRequest: BadRequestHandler,
    val badGateway: BadGatewayHandler,
    val gatewayTimeout: GatewayTimeoutHandler,
    val invalidData: InvalidDataHandler
)

data class UnknownHandler(
    val unknown: String,
    val unexpectedError: String,
    val unidentifiedError: String
)

data class NotFoundHandler(
    val resource: String,
    val couldNotFind: String
)

data class BadRequestHandler(
    val invalidFiel: String,
    val malformedRequest: String
)

data class BadGatewayHandler(
    val invalidGateway: String,
    val gatewayTimeout: String,
    val intermediaryServer: String
)

data class GatewayTimeoutHandler(
    val tryAgainLater: String
)

data class InvalidDataHandler(
    val obtainedInvalid: String
)

