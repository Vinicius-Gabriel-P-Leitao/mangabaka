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
import jakarta.persistence.Id

@Entity
open class FrontendTranslation {
    @Id
    open val id: Long? = null

    @DbJsonB
    @Column(columnDefinition = "jsonb")
    open val translations: MutableMap<String?, Any?>? = null
}