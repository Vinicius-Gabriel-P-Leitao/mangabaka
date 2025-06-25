/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.exception.code.custom

import br.mangabaka.exception.code.ErrorCodeProvider

enum class AssetDownloadErrorCode : ErrorCodeProvider {
    ERROR_CLIENT_STATUS {
        override fun handle(value: String) = "Erro ao baixar assets: $value"
    },

    ERROR_INVALID_URL {
        override fun handle(value: String) = "Erro ao baixar assets, Url invalida: $value"
    },

    ERROR_TIMEOUT {
        override fun handle(value: String) = "Erro ao baixar assets, timeout tempo foi excedido na requisiçaõ: $value"
    },

    ERROR_CLIENT_EXCEPTION {
        override fun handle(value: String) = "Erro ao baixar assets: $value"
    },

    ERROR_EMPTY_DATA {
        override fun handle(value: String) = "Erro ao baixar assets, dados vázios: $value"
    }
}