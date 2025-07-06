/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.exception.code.custom

import br.mangabaka.exception.code.ErrorCodeProvider
import br.mangabaka.infrastructure.config.singleton.I18n

enum class GraphqlErrorCode : ErrorCodeProvider {
    ERROR_CLIENT {
        override fun handle(value: String) = I18n.get("error.code.graphql.client", value)
    },

    ERROR_TIMEOUT {
        override fun handle(value: String) = I18n.get("error.code.graphql.client.timeout", value)
    },

    ERROR_EMPTY_RESPONSE {
        override fun handle(value: String) = I18n.get("error.code.graphql.empty.response", value)
    },

    ERROR_INVALID_URL {
        override fun handle(value: String) = I18n.get("error.code.graphql.invalid.url", value)
    }
}