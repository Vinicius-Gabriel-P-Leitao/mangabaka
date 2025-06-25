/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.exception.code.custom

import br.mangabaka.exception.code.ErrorCodeProvider

enum class InvalidParameterErrorCode : ErrorCodeProvider {
    ERROR_PARAMETER_EMPTY {
        override fun handle(value: String) = "Erro de parametro vazio: $value"
    },

    ERROR_PARAMETER_INVALID {
        override fun handle(value: String) = "O parametro é invalido: $value"
    },
}