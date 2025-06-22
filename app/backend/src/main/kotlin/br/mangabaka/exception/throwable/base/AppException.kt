/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.exception.throwable.base

import br.mangabaka.exception.code.ErrorCodeProvider

// @formatter:off
open class AppException(message: String, val errorCode: ErrorCodeProvider, cause: Throwable? = null) : IllegalStateException(message) {
    override fun toString(): String {
        return "AppException(errorCode=${errorCode.javaClass.simpleName}, message=$message)"
    }
}