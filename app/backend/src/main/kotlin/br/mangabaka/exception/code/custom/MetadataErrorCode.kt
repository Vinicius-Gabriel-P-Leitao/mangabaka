/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.exception.code.custom;

import br.mangabaka.exception.code.ErrorCodeProvider

enum class MetadataErrorCode : ErrorCodeProvider {
    ERROR_FIELD_EMPTY {
        override fun handle(value: String) = "Erro de campo vazio: $value"
    },

    ERROR_JSON_MALFORMED {
        override fun handle(value: String) = "Erro de campo vazio: $value"
    },
}