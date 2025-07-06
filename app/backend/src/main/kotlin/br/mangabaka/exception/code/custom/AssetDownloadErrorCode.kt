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

enum class AssetDownloadErrorCode : ErrorCodeProvider {
    ERROR_CLIENT_STATUS {
        override fun handle(value: String) = I18n.get("error.code.asset.download", value)
    },

    ERROR_INVALID_URL {
        override fun handle(value: String) = I18n.get("error.code.asset.invalid.url", value)
    },

    ERROR_TIMEOUT {
        override fun handle(value: String) = I18n.get("error.code.asset.download.timeout", value)
    },

    ERROR_CLIENT_EXCEPTION {
        override fun handle(value: String) = I18n.get("error.code.asset.download.exception", value)
    },

    ERROR_EMPTY_DATA {
        override fun handle(value: String) = I18n.get("error.code.asset.download.empty.data", value)
    }
}