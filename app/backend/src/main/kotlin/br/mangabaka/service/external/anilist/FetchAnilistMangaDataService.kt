/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.external.anilist

import br.mangabaka.api.dto.MangaDataDto
import br.mangabaka.infrastructure.http.anilist.dto.anilist.MangaPaginatedMetadataDto
import br.mangabaka.infrastructure.http.anilist.query.MangaPaginatedQuery
import br.mangabaka.service.external.ExternalMetadataService
import br.mangabaka.service.internal.MangaResolverService.Companion.PAGE
import br.mangabaka.service.internal.MangaResolverService.Companion.PER_PAGE

class FetchAnilistMangaDataService(
    private val query: MangaPaginatedQuery = MangaPaginatedQuery(),
) : ExternalMetadataService {
    override fun fetchMangaData(mangaName: String): MangaDataDto {
        val mangaMetadata: MangaPaginatedMetadataDto = query.queryAllDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)

        // TODO: Validar dados antes de mandar

        return MangaDataDto(paginationInfo = mangaMetadata, assets = null)
    }
}