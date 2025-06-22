/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.dto

import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
import br.mangabaka.infrastructure.http.anilist.dto.anilist.MangaPaginatedDto

data class MangaMetadata(
    val paginationInfo: MangaPaginatedDto,
    val assets: List<DownloadedAssetDto>
)
