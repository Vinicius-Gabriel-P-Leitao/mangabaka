/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.exception.code.http

import br.mangabaka.exception.code.ErrorCodeProvider

enum class GraphqlErrorCode : ErrorCodeProvider {
    ERROR_CLIENT {
        override fun handle(value: String) = "Erro no Graphql: $value"
    },

    ERROR_TIMEOUT {
        override fun handle(value: String) = "Erro no Graphql, timeout tempo foi excedido na requisiçaõ: $value"
    },

    ERROR_EMPTY_RESPONSE {
        override fun handle(value: String) = "Erro no Graphql, valor vázio: $value"
    },

    ERROR_INVALID_URL {
        override fun handle(value: String) = "Erro no Graphql, url é invalida: $value"
    }
}