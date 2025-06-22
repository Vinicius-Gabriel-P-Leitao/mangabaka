/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.exception.code.http

import br.mangabaka.exception.code.ErrorCodeProvider

enum class AssetDownloadErrorCode : ErrorCodeProvider {
    ERROR_CLIENT_STATUS {
        override fun handle(value: String) = "Erro no cliente http: $value"
    },

    ERROR_INVALID_URL {
        override fun handle(value: String) = "Url invalida: $value"
    },

    ERROR_CLIENT_EXCEPTION {
        override fun handle(value: String) = "Erro genérico lançado: $value"
    }
}