/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.usecase;

import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.code.custom.SqlErrorCode
import br.mangabaka.exception.throwable.base.AppException
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.exception.throwable.http.SqlException
import frontend.translation.dto.*
import frontend.translation.model.FrontendTranslation
import jakarta.ws.rs.core.Response
import kotlinx.serialization.SerializationException
import java.sql.SQLException

abstract class FrontendTranslationService {
    data class I18nPair(
        val serializationMessage: String,
        val errorCodeIsNullMessage: String,
        val defaultErrorMessage: String,
    )

    internal inline fun <T> handlerBaseMethod(action: () -> T, i18nPair: I18nPair): T {
        return try {
            action()
        } catch (serializationException: SerializationException) {
            val missingFieldRegex = Regex(pattern = """Field '(.*?)' is required""")
            val campo = missingFieldRegex.find(input = serializationException.message ?: "")?.groupValues?.get(1)

            throw MetadataException(
                message = MetadataErrorCode.ERROR_JSON_MALFORMED.handle(value = i18nPair.serializationMessage + campo),
                errorCode = MetadataErrorCode.ERROR_JSON_MALFORMED,
                httpError = Response.Status.BAD_REQUEST
            )
        } catch (exception: Exception) {
            if (exception is SQLException || exception.cause is SQLException) {
                val sqlException = exception as? SQLException ?: exception.cause as? SQLException
                val errorCode = sqlException?.sqlState?.let { SqlErrorCode.mapSqlStateToErrorCode(sqlState = it) }

                if (errorCode == null) {
                    throw InternalException(
                        message = InternalErrorCode.ERROR_INTERNAL_SQL.handle(value = i18nPair.errorCodeIsNullMessage + exception.localizedMessage),
                        errorCode = InternalErrorCode.ERROR_INTERNAL_SQL,
                    )
                }

                throw SqlException(
                    message = errorCode.handle(value = sqlException.sqlState),
                    errorCode = errorCode,
                    httpError = Response.Status.BAD_REQUEST
                )
            }

            when (exception) {
                is AppException -> throw exception
                else -> throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(value = i18nPair.defaultErrorMessage),
                    errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
    }

    fun toI18nJsonFormat(translations: List<FrontendTranslation>): I18nJsonFormat {
        val flatMap = translations.associate { it.translationKey to it.translationValue }

        return I18nJsonFormat(
            meta = Meta(
                code = flatMap["meta.code"] ?: "",
                label = flatMap["meta.label"] ?: ""
            ),

            page = Page(
                notFound = ErrorPage(
                    title = flatMap["page.notFound.title"] ?: "",
                    message = flatMap["page.notFound.message"] ?: "",
                    cause = flatMap["page.notFound.cause"] ?: "",
                    imageAlt = flatMap["page.notFound.imageAlt"] ?: ""
                ), internalServerError = ErrorPage(
                    title = flatMap["page.internalServerError.title"] ?: "",
                    message = flatMap["page.internalServerError.message"] ?: "",
                    cause = flatMap["page.internalServerError.cause"] ?: "",
                    imageAlt = flatMap["page.internalServerError.imageAlt"] ?: ""
                ), gatewayTimeout = ErrorPage(
                    title = flatMap["page.gatewayTimeout.title"] ?: "",
                    message = flatMap["page.gatewayTimeout.message"] ?: "",
                    cause = flatMap["page.gatewayTimeout.cause"] ?: "",
                    imageAlt = flatMap["page.gatewayTimeout.imageAlt"] ?: ""
                ), badRequest = ErrorPage(
                    title = flatMap["page.badRequest.title"] ?: "",
                    message = flatMap["page.badRequest.message"] ?: "",
                    cause = flatMap["page.badRequest.cause"] ?: "",
                    imageAlt = flatMap["page.badRequest.imageAlt"] ?: ""
                ), badGateway = ErrorPage(
                    title = flatMap["page.badGateway.title"] ?: "",
                    message = flatMap["page.badGateway.message"] ?: "",
                    cause = flatMap["page.badGateway.cause"] ?: "",
                    imageAlt = flatMap["page.badGateway.imageAlt"] ?: ""
                ), conflict = ErrorPage(
                    title = flatMap["page.conflict.title"] ?: "",
                    message = flatMap["page.conflict.message"] ?: "",
                    cause = flatMap["page.conflict.cause"] ?: "",
                    imageAlt = flatMap["page.conflict.imageAlt"] ?: ""
                ), forbidden = ErrorPage(
                    title = flatMap["page.forbidden.title"] ?: "",
                    message = flatMap["page.forbidden.message"] ?: "",
                    cause = flatMap["page.forbidden.cause"] ?: "",
                    imageAlt = flatMap["page.forbidden.imageAlt"] ?: ""
                ), unavailable = ErrorPage(
                    title = flatMap["page.unavailable.title"] ?: "",
                    message = flatMap["page.unavailable.message"] ?: "",
                    cause = flatMap["page.unavailable.cause"] ?: "",
                    imageAlt = flatMap["page.unavailable.imageAlt"] ?: ""
                ), methodNotAllowed = ErrorPage(
                    title = flatMap["page.methodNotAllowed.title"] ?: "",
                    message = flatMap["page.methodNotAllowed.message"] ?: "",
                    cause = flatMap["page.methodNotAllowed.cause"] ?: "",
                    imageAlt = flatMap["page.methodNotAllowed.imageAlt"] ?: ""
                ), home = HomePage(
                    title = flatMap["page.home.title"] ?: ""
                )
            ),

            component = Component(
                translation = Translation(
                    infoView = flatMap["component.translation.infoView"] ?: ""
                ), select = Label(
                    label = flatMap["component.select.label"] ?: ""
                )
            ),

            handler = Handler(
                unknown = UnknownHandler(
                    unknown = flatMap["handler.unknown.unknown"] ?: "",
                    unexpectedError = flatMap["handler.unknown.unexpectedError"] ?: "",
                    unidentifiedError = flatMap["handler.unknown.unidentifiedError"] ?: ""
                ), notFound = NotFoundHandler(
                    resource = flatMap["handler.notFound.resource"] ?: "",
                    couldNotFind = flatMap["handler.notFound.couldNotFind"] ?: ""
                ), badRequest = BadRequestHandler(
                    invalidField = flatMap["handler.badRequest.invalidField"] ?: "",
                    malformedRequest = flatMap["handler.badRequest.malformedRequest"] ?: ""
                ), badGateway = BadGatewayHandler(
                    invalidGateway = flatMap["handler.badGateway.invalidGateway"] ?: "",
                    gatewayTimeout = flatMap["handler.badGateway.gatewayTimeout"] ?: "",
                    intermediaryServer = flatMap["handler.badGateway.intermediaryServer"] ?: ""
                ), gatewayTimeout = GatewayTimeoutHandler(
                    tryAgainLater = flatMap["handler.gatewayTimeout.tryAgainLater"] ?: ""
                ), invalidData = InvalidDataHandler(
                    obtainedInvalid = flatMap["handler.invalidData.obtainedInvalid"] ?: ""
                )
            )
        )
    }
}