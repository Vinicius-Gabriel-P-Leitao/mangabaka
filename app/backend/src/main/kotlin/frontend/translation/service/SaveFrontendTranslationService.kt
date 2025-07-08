/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.service

import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.code.custom.SqlErrorCode
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.exception.throwable.http.SqlException
import br.mangabaka.infrastructure.config.singleton.I18n
import br.mangabaka.infrastructure.config.singleton.JsonConfig
import frontend.translation.dto.I18nJsonFormat
import frontend.translation.model.FrontendTranslation
import frontend.translation.repository.FrontendTranslationRepo
import frontend.translation.usecase.FrontendTranslationService
import jakarta.ws.rs.core.Response
import kotlinx.serialization.json.*

class SaveFrontendTranslationService(
    private val repository: FrontendTranslationRepo,
) : FrontendTranslationService() {
    fun saveTranslation(data: String): I18nJsonFormat {
        return handlerBaseMethod(
            action = {
                val i18nObject: I18nJsonFormat = JsonConfig.jsonParser.decodeFromString<I18nJsonFormat>(string = data)
                if (i18nObject.meta.label.isBlank() || i18nObject.meta.code.isBlank()) {
                    throw MetadataException(
                        message = MetadataErrorCode.ERROR_EMPTY_FIELD.handle(value = I18n.get(key = "throw.malformed.json.meta.language.translation")),
                        errorCode = MetadataErrorCode.ERROR_EMPTY_FIELD,
                        httpError = Response.Status.BAD_REQUEST
                    )
                }

                val json = Json { ignoreUnknownKeys = true }
                val jsonElement: JsonElement = json.encodeToJsonElement(value = i18nObject)
                val flatMap = mutableMapOf<String, String>()
                flattenJson(prefix = "", element = jsonElement, map = flatMap)

                val callbackSave: MutableList<FrontendTranslation> = mutableListOf()

                val lang = i18nObject.meta.label
                val code = i18nObject.meta.code

                for ((key, value) in flatMap) {
                    val existing: FrontendTranslation? = repository.findByKeyAndLang(key, lang)
                    if (existing != null) {
                        existing.translationValue = value
                        val saved: FrontendTranslation? = repository.save(entity = existing)
                        if (saved != null) {
                            callbackSave.add(saved)
                        }
                    } else {
                        val saved: FrontendTranslation? = repository.save(
                            entity = FrontendTranslation(
                                codeLanguage = code, metaLanguage = lang, translationKey = key, translationValue = value
                            )
                        )

                        if (saved == null) {
                            throw MetadataException(
                                message = MetadataErrorCode.ERROR_EMPTY_FIELD.handle(value = I18n.get(key = "throw.database.callback.data.is.empty.or.null")),
                                errorCode = MetadataErrorCode.ERROR_EMPTY_FIELD,
                                httpError = Response.Status.BAD_REQUEST
                            )
                        }

                        callbackSave.add(saved)
                    }
                }

                if (callbackSave.isEmpty()) {
                    throw SqlException(
                        message = SqlErrorCode.ERROR_PERSIST_DATA.handle(value = I18n.get(key = "throw.data.callback.is.null")),
                        errorCode = SqlErrorCode.ERROR_PERSIST_DATA,
                        httpError = Response.Status.INTERNAL_SERVER_ERROR
                    )
                }

                toI18nJsonFormat(translations = callbackSave)
            }, i18nPair = I18nPair(
                serializationMessage = I18n.get("throw.malformed.serialization.json", I18n.get("throw.unknown.error")),
                errorCodeIsNullMessage = I18n.get("throw.internal.saving.translation", I18n.get("throw.unknown.error")),
                defaultErrorMessage = I18n.get("throw.internal.saving.translation", I18n.get("throw.unknown.error"))
            )
        )
    }

    fun flattenJson(prefix: String, element: JsonElement, map: MutableMap<String, String>) {
        when (element) {
            is JsonObject -> {
                for ((key, value) in element) {
                    flattenJson(
                        prefix = if (prefix.isEmpty()) key else "$prefix.$key", element = value, map
                    )
                }
            }

            is JsonPrimitive -> {
                map[prefix] = element.content
            }

            is JsonArray -> {
                element.forEachIndexed { index, item ->
                    flattenJson(prefix = "$prefix[$index]", element = item, map)
                }
            }
        }
    }
}