/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.dto

import kotlinx.serialization.Serializable

@Serializable
data class I18nJsonFormat(
    val meta: Meta,
    val page: Page,
    val component: Component,
    val handler: Handler,
)

@Serializable
data class Meta(
    val code: String,
    val label: String,
)

@Serializable
data class Page(
    val notFound: ErrorPage,
    val internalServerError: ErrorPage,
    val gatewayTimeout: ErrorPage,
    val badRequest: ErrorPage,
    val badGateway: ErrorPage,
    val conflict: ErrorPage,
    val forbidden: ErrorPage,
    val unavailable: ErrorPage,
    val methodNotAllowed: ErrorPage,
    val home: HomePage,
)

@Serializable
data class ErrorPage(
    val title: String,
    val message: String,
    val cause: String,
    val imageAlt: String,
)

@Serializable
data class HomePage(
    val title: String,
)

@Serializable
data class Component(
    val translation: Translation,
    val select: Label,
)

@Serializable
data class Translation(
    val infoView: String,
)

@Serializable
data class Label(
    val label: String,
)

@Serializable
data class Handler(
    val unknown: UnknownHandler,
    val notFound: NotFoundHandler,
    val badRequest: BadRequestHandler,
    val badGateway: BadGatewayHandler,
    val gatewayTimeout: GatewayTimeoutHandler,
    val invalidData: InvalidDataHandler,
)

@Serializable
data class UnknownHandler(
    val unknown: String,
    val unexpectedError: String,
    val unidentifiedError: String,
)

@Serializable
data class NotFoundHandler(
    val resource: String,
    val couldNotFind: String,
)

@Serializable
data class BadRequestHandler(
    val invalidField: String,
    val malformedRequest: String,
)

@Serializable
data class BadGatewayHandler(
    val invalidGateway: String,
    val gatewayTimeout: String,
    val intermediaryServer: String,
)

@Serializable
data class GatewayTimeoutHandler(
    val tryAgainLater: String,
)

@Serializable
data class InvalidDataHandler(
    val obtainedInvalid: String,
)



