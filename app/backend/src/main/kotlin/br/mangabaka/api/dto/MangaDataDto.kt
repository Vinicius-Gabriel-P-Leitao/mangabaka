/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.api.dto

import br.mangabaka.infrastructure.http.anilist.dto.DownloadedAssetDto
import br.mangabaka.infrastructure.http.anilist.dto.MangaPaginatedMetadataDto

data class MangaDataDto(
    val paginationInfo: MangaPaginatedMetadataDto?,
    val asset: List<DownloadedAssetDto>?
)
