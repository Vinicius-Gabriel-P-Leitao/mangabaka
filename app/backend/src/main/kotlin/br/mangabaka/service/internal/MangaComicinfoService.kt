/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.internal

import br.mangabaka.service.external.anilist.FetchAnilistMangaDataService
import jakarta.ws.rs.core.Response

class MangaComicinfoService {
    fun createComicinfo(nameManga: String): Response.Status {
        val fetchAnilistMangaDataService = FetchAnilistMangaDataService()
        val resolverService = MangaResolverService(fetchAnilistMangaDataService)

        return Response.Status.CREATED
    }
}