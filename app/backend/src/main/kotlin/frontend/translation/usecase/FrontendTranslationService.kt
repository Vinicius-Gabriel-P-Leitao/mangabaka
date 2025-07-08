/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.usecase;

import br.mangabaka.exception.code.custom.InternalErrorCode
import br.mangabaka.exception.code.custom.InvalidDataErrorCode
import br.mangabaka.exception.code.custom.MetadataErrorCode
import br.mangabaka.exception.code.custom.SqlErrorCode
import br.mangabaka.exception.throwable.base.AppException
import br.mangabaka.exception.throwable.base.InternalException
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.exception.throwable.http.SqlException
import br.mangabaka.infrastructure.config.singleton.I18n
import frontend.translation.dto.*
import frontend.translation.model.FrontendTranslation
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.Response
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import java.sql.SQLException

@Suppress("UNCHECKED_CAST")
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
        val flatMap: Map<String, String> = translations.associate { it.translationKey to it.translationValue }
        val nestedMap = mutableMapOf<String, Any>()

        for ((key, value) in flatMap) {
            val keys: List<String> = key.split(".")
            var currentLevel: MutableMap<String, Any> = nestedMap

            for (item in 0 until keys.size - 1) {
                val parts: String = keys[item]
                val nextLevel: Any? = currentLevel[parts]

                if (nextLevel == null || nextLevel !is MutableMap<*, *>) {
                    val newMap = mutableMapOf<String, Any>()
                    currentLevel[parts] = newMap
                    currentLevel = newMap
                } else {
                    @Suppress("UNCHECKED_CAST")
                    currentLevel = nextLevel as MutableMap<String, Any>
                }
            }

            currentLevel[keys.last()] = value
        }

        val json = Json { ignoreUnknownKeys = true }
        val jsonElement = mapToJsonElement(map = nestedMap, depth = nestedMap.size)
        val jsonString = Json.encodeToString(value = jsonElement)
        return json.decodeFromString<I18nJsonFormat>(string = jsonString)
    }

    fun mapToJsonElement(map: Map<String, Any>, depth: Int = 0): JsonObject {
        if (depth > 20) throw BadRequestException(
            InvalidDataErrorCode.ERROR_SIZE_DATA.handle(value = I18n.get(key = "throw.json.depth.is.large"))
        )

        val content = map.mapValues { (_, value) ->
            when (value) {
                is String -> JsonPrimitive(value)
                is Map<*, *> -> mapToJsonElement(map = value as Map<String, Any>, depth = value.size)
                else -> JsonNull
            }
        }
        return JsonObject(content)
    }
}