/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package frontend.translation.service

import br.mangabaka.infrastructure.config.singleton.JsonConfig
import frontend.translation.dto.I18nJsonFormat
import frontend.translation.model.FrontendTranslation
import frontend.translation.repository.FrontendTranslationRepo
import kotlinx.serialization.json.*

class FrontendTranslationService(
    private val repository: FrontendTranslationRepo
) {

    fun saveTranslation(
        i18nObject: I18nJsonFormat
    ) {
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
                val newEntry = FrontendTranslation(metaLanguage = lang, translationKey = key, translationValue = value)
                repository.save(entity = newEntry)
            }
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