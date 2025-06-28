/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.repository;

import br.mangabaka.domain.EbeanRepository
import frontend.translation.model.FrontendTranslation
import frontend.translation.model.FrontendTranslation.Companion.META_LANGUAGE
import frontend.translation.model.FrontendTranslation.Companion.TRANSLATION_KEY
import io.ebean.DB

class FrontendTranslationRepo : EbeanRepository<Long, FrontendTranslation>(entityClass = FrontendTranslation::class) {
    fun findByKeyAndLang(key: String, lang: String): FrontendTranslation? {
        return DB.find(FrontendTranslation::class.java)
            .where()
            .eq(META_LANGUAGE, lang)
            .eq(TRANSLATION_KEY, key)
            .findOne()
    }

    fun findByLang(lang: String): List<FrontendTranslation> {
        return DB.find(FrontendTranslation::class.java)
            .where()
            .eq(META_LANGUAGE, lang)
            .findList()
    }
}