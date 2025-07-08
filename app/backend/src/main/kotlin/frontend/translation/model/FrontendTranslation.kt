/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.model

import br.mangabaka.infrastructure.config.database.PostgresqlConfig.Companion.SCHEMA
import jakarta.persistence.*
import java.time.ZoneId
import java.time.ZonedDateTime

@Entity
@Table(
    name = "tb_frontend_translation", schema = SCHEMA
)
class FrontendTranslation(
    codeLanguage: String,
    metaLanguage: String,
    translationKey: String,
    translationValue: String,
) {
    companion object {
        const val ID_TRANSLATION = "id_translation"
        const val CODE_LANGUAGE = "code_language"
        const val META_LANGUAGE = "meta_language"
        const val TRANSLATION_KEY = "translation_key"
        const val TRANSLATION_VALUE = "translation_value"
        const val UPDATE_AT = "updated_at"
    }

    @Id
    @Column(name = ID_TRANSLATION)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = CODE_LANGUAGE, length = 10, nullable = false)
    var codeLanguage: String = codeLanguage

    @Column(name = META_LANGUAGE, length = 100, nullable = false)
    var metaLanguage: String = metaLanguage

    @Column(name = TRANSLATION_KEY, length = 255, nullable = false)
    var translationKey: String = translationKey

    @Column(name = TRANSLATION_VALUE, columnDefinition = "TEXT", nullable = false)
    var translationValue: String = translationValue

    @Column(name = UPDATE_AT, columnDefinition = "TIMESTAMPTZ", nullable = false)
    var updatedAt: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault())
}