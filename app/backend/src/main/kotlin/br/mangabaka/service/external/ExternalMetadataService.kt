/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.external

import br.mangabaka.api.dto.MangaDataDto

interface ExternalMetadataService {
    fun fetchMangaData(mangaName: String): MangaDataDto
}