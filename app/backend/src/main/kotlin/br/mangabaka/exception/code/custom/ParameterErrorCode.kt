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

enum class ParameterErrorCode : ErrorCodeProvider {
    ERROR_PARAMETER_EMPTY {
        override fun handle(value: String) = I18n.get("error.code.empty.parameter", value)
    },

    ERROR_PARAMETER_INVALID {
        override fun handle(value: String) = I18n.get("error.code.invalid.parameter", value)
    },

    ERROR_BODY_EMPTY {
        override fun handle(value: String) = I18n.get("error.code.empty.body", value)
    },
}