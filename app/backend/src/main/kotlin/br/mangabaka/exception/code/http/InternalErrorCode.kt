/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.exception.code.http

import br.mangabaka.exception.code.ErrorCodeProvider

enum class InternalErrorCode : ErrorCodeProvider {
    ERROR_INTERNAL_GENERIC {
        override fun handle(value: String) = "Erro interno de servidor: $value"
    },
}