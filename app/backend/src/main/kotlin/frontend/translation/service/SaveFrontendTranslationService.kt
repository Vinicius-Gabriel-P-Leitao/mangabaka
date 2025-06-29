/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.service

import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.code.custom.SqlErrorCode
import br.mangabaka.exception.throwable.base.AppException
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.exception.throwable.http.SqlException
import br.mangabaka.infrastructure.config.singleton.I18n
import br.mangabaka.infrastructure.config.singleton.JsonConfig
import frontend.translation.dto.I18nJsonFormat
import frontend.translation.model.FrontendTranslation
import frontend.translation.repository.FrontendTranslationRepo
import jakarta.ws.rs.core.Response
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*
import java.sql.SQLException

class SaveFrontendTranslationService(
    private val repository: FrontendTranslationRepo,
    private val fetchFrontendTranslationService: FetchFrontendTranslationService = FetchFrontendTranslationService(),
) {
    fun saveTranslation(data: String): I18nJsonFormat {
        try {
            val i18nObject: I18nJsonFormat = JsonConfig.jsonParser.decodeFromString<I18nJsonFormat>(data)
            if (i18nObject.meta.language.isBlank()) {
                throw MetadataException(
                    message = MetadataErrorCode.ERROR_JSON_MALFORMED.handle(value = I18n.get("throw.malformed.json.meta.language.translation")),
                    errorCode = MetadataErrorCode.ERROR_JSON_MALFORMED,
                    httpError = Response.Status.BAD_REQUEST
                )
            }

            val json = Json { ignoreUnknownKeys = true }
            val jsonElement: JsonElement = json.encodeToJsonElement(value = i18nObject)
            val flatMap = mutableMapOf<String, String>()
            flattenJson(prefix = "", element = jsonElement, map = flatMap)


            val callbackSave: MutableList<FrontendTranslation> = mutableListOf()
            val lang = i18nObject.meta.language
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
                            metaLanguage = lang, translationKey = key, translationValue = value
                        )
                    )

                    if (saved != null) {
                        callbackSave.add(saved)
                    }
                }
            }

            if (callbackSave.isEmpty()) {
                throw SqlException(
                    message = SqlErrorCode.ERROR_PERSIST_DATA.handle(I18n.get("throw.data.callback.is.null")),
                    errorCode = SqlErrorCode.ERROR_PERSIST_DATA,
                    httpError = Response.Status.INTERNAL_SERVER_ERROR
                )
            }

            return fetchFrontendTranslationService.toI18nJsonFormat(translations = callbackSave)
        } catch (serializationException: SerializationException) {
            val missingFieldRegex = Regex("""Field '(.*?)' is required""")
            val campo = missingFieldRegex.find(serializationException.message ?: "")?.groupValues?.get(1)

            throw MetadataException(
                message = MetadataErrorCode.ERROR_JSON_MALFORMED.handle(
                    value = I18n.get(
                        "throw.malformed.serialization.json", campo ?: I18n.get("throw.unknown.error")
                    )
                ), errorCode = MetadataErrorCode.ERROR_JSON_MALFORMED, httpError = Response.Status.BAD_REQUEST
            )
        } catch (exception: Exception) {
            if (exception is SQLException || exception.cause is SQLException) {
                val sqlException = exception as? SQLException ?: exception.cause as? SQLException
                val errorCode = sqlException?.sqlState?.let { SqlErrorCode.mapSqlStateToErrorCode(sqlState = it) }

                if (errorCode == null) {
                    throw InternalException(
                        message = InternalErrorCode.ERROR_INTERNAL_SQL.handle(
                            value = I18n.get(
                                key = "throw.error.fetch.metadata",
                                exception.localizedMessage ?: I18n.get(key = "throw.unknown.error")
                            )
                        ), errorCode = InternalErrorCode.ERROR_INTERNAL_SQL
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
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(
                        value = I18n.get(
                            key = "throw.internal.saving.translation", I18n.get(key = "throw.unknown.error")
                        )
                    ), errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }
        }
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