/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.service;

import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.infrastructure.config.singleton.I18n
import frontend.translation.dto.AvailableTranslationsDto
import frontend.translation.dto.I18nJsonFormat
import frontend.translation.model.FrontendTranslation
import frontend.translation.repository.FrontendTranslationRepo
import frontend.translation.usecase.FrontendTranslationService
import jakarta.ws.rs.core.Response

class FetchFrontendTranslationService(
    private val repository: FrontendTranslationRepo,
) : FrontendTranslationService() {

    fun fetchAvailableTranslations(): List<AvailableTranslationsDto> {
        return handlerBaseMethod(
            action = {
                val fetched: List<FrontendTranslation>? = repository.findAllMetaLanguage()
                if (fetched.isNullOrEmpty()) {
                    throw MetadataException(
                        message = MetadataErrorCode.ERROR_EMPTY_FIELD.handle(value = I18n.get(key = "throw.database.callback.data.is.empty.or.null")),
                        errorCode = MetadataErrorCode.ERROR_EMPTY_FIELD,
                        httpError = Response.Status.BAD_REQUEST
                    )
                }

                fetched.map {
                    AvailableTranslationsDto(
                        code = it.codeLanguage,
                        label = it.metaLanguage
                    )
                }
            },
            i18nPair = I18nPair(
                serializationMessage = I18n.get("throw.malformed.serialization.json", I18n.get("throw.unknown.error")),
                errorCodeIsNullMessage = I18n.get("throw.internal.fetch.translation", I18n.get("throw.unknown.error")),
                defaultErrorMessage = I18n.get("throw.internal.fetch.translation", I18n.get("throw.unknown.error"))
            )
        )
    }

    fun fetchDataTranslation(lang: String): I18nJsonFormat {
        return handlerBaseMethod(
            action = {
                val fetched: List<FrontendTranslation>? = repository.findByLang(lang)
                if (fetched.isNullOrEmpty()) {
                    throw MetadataException(
                        message = MetadataErrorCode.ERROR_EMPTY_FIELD.handle(value = I18n.get(key = "throw.database.callback.data.is.empty.or.null")),
                        errorCode = MetadataErrorCode.ERROR_EMPTY_FIELD,
                        httpError = Response.Status.BAD_REQUEST
                    )
                }

                val i18nObject: I18nJsonFormat = toI18nJsonFormat(translations = fetched)
                if (i18nObject.meta.label.isBlank() || i18nObject.meta.code.isBlank()) {
                    throw MetadataException(
                        message = MetadataErrorCode.ERROR_JSON_MALFORMED.handle(value = I18n.get(key = "throw.malformed.json.meta.language.translation")),
                        errorCode = MetadataErrorCode.ERROR_JSON_MALFORMED,
                        httpError = Response.Status.BAD_REQUEST
                    )
                }

                i18nObject
            },
            i18nPair = I18nPair(
                serializationMessage = I18n.get("throw.malformed.serialization.json", I18n.get("throw.unknown.error")),
                errorCodeIsNullMessage = I18n.get("throw.internal.fetch.translation", I18n.get("throw.unknown.error")),
                defaultErrorMessage = I18n.get("throw.internal.fetch.translation", I18n.get("throw.unknown.error"))
            )
        )
    }
}