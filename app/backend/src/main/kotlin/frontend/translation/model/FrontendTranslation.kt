/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package frontend.translation.model

import io.ebean.annotation.DbJsonB
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tb_frontend_translation", schema = "mangabaka")
open class FrontendTranslation {
    @Id
    @Column(name = "id_translation")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long? = null

    @Column(name = "meta_language")
    open val metaLanguage: String? = null

    @DbJsonB
    @Column(name = "json_translation", columnDefinition = "jsonb")
    open val translations: MutableMap<String?, Any?>? = null
}