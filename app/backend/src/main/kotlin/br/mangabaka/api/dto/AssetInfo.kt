/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.dto

enum class AssetType(val code: String) {
    COVER("cover"), BANNER("banner");
}

data class AssetInfo(
    val url: String, val mangaName: String, val type: AssetType
)