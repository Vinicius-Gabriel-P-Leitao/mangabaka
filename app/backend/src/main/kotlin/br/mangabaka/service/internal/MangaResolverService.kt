/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.internal

import br.mangabaka.api.dto.MangaDataDto
import br.mangabaka.service.external.ExternalMetadataService

class MangaResolverService(
    private val services: ExternalMetadataService
) {
    companion object {
        const val PAGE: Int = 1
        const val PER_PAGE: Int = 1
    }

    fun mangaResolver(mangaName: String): MangaDataDto {
        // TODO: Fazer todo tratamento de dados após fazer toda parte do mangadex e testes
        //  após fazer mangádex e rotas de dados e estiver como o anilist funcionando perfeitamente
        //  e essa classe também iniciar parte do serviço de ComicInfo e rotas do comicinfo talvez
        val result: MangaDataDto = services.fetchMangaData(mangaName)
        return result
    }
}