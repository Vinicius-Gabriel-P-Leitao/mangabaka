/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.exception.throwable.http;

import br.mangabaka.exception.code.ErrorCodeProvider
import br.mangabaka.exception.throwable.base.AppException

// @formatter:off
class MetadataException(message: String, errorCode: ErrorCodeProvider) : AppException(message, errorCode)