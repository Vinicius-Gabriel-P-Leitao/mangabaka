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

class FrontendTranslationService(
    private val repository: FrontendTranslationRepo
) {
    fun saveTranslation(
        data: String
    ) {
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

            val lang = i18nObject.meta.language
            for ((key, value) in flatMap) {
                val existing = repository.findByKeyAndLang(key, lang)
                if (existing != null) {
                    existing.translationValue = value
                    repository.save(entity = existing)
                } else {
                    val newEntry =
                        FrontendTranslation(metaLanguage = lang, translationKey = key, translationValue = value)
                    repository.save(entity = newEntry)
                }
            }
        } catch (serializationException: SerializationException) {
            val missingFieldRegex = Regex("""Field '(.*?)' is required""")
            val campo = missingFieldRegex.find(serializationException.message ?: "")?.groupValues?.get(1)

            throw MetadataException(
                message = MetadataErrorCode.ERROR_JSON_MALFORMED.handle(
                    value = I18n.get(
                        "throw.malformed.serialization.json",
                        campo ?: I18n.get("throw.unknown.error")
                    )
                ), errorCode = MetadataErrorCode.ERROR_JSON_MALFORMED, httpError = Response.Status.BAD_REQUEST
            )
        } catch (sqlException: SQLException) {
            val errorCode = SqlErrorCode.mapSqlStateToErrorCode(sqlState = sqlException.sqlState)
            if (errorCode == null) {
                throw InternalException(
                    message = InternalErrorCode.ERROR_INTERNAL_GENERIC.handle(
                        value = I18n.get(
                            "throw.error.fetch.metadata",
                            sqlException.localizedMessage ?: I18n.get("throw.unknown.error")
                        )
                    ), errorCode = InternalErrorCode.ERROR_INTERNAL_GENERIC
                )
            }

            throw SqlException(
                message = errorCode.handle(sqlException.sqlState),
                errorCode = errorCode, httpError = Response.Status.BAD_REQUEST
            )
        }
    }

    fun flattenJson(prefix: String, element: JsonElement, map: MutableMap<String, String>) {
        when (element) {
            is JsonObject -> {
                for ((key, value) in element) {
                    flattenJson(
                        prefix = if (prefix.isEmpty()) key else "$prefix.$key",
                        element = value,
                        map
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