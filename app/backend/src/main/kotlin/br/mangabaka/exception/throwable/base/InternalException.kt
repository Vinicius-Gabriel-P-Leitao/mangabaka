/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.exception.throwable.base

import br.mangabaka.exception.code.ErrorCodeProvider
import jakarta.ws.rs.core.Response

// @formatter:off
class InternalException(message: String, errorCode: ErrorCodeProvider, cause: Throwable? = null)
    : AppException(message, errorCode, cause)