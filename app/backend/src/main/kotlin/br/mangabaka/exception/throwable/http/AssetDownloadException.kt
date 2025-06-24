/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.exception.throwable.http

import br.mangabaka.exception.code.ErrorCodeProvider
import br.mangabaka.exception.throwable.base.AppException
import jakarta.ws.rs.core.Response

// @formatter:off
class AssetDownloadException(message: String, errorCode: ErrorCodeProvider, httpError: Response.Status, cause: Throwable? = null)
    : AppException(message, errorCode, httpError, cause)