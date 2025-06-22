/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.internal

import br.mangabaka.api.dto.MangaMetadata
import br.mangabaka.service.external.ExternalMetadataService
import java.nio.file.Paths

class MangaResolverService(private val services: List<ExternalMetadataService>) {
    companion object {
        private const val PATH_FILE: String = "/app/data/mangabaka"
    }

    fun mangaResolver(mangaName: String): MangaMetadata {
        val result: List<MangaMetadata> = services.map { it.fetchMangaData(mangaName) }

        // TODO: Implementar salvamento no banco de dados e container os arquivos, covers e banners
        //  não vão ser armazenados de forma fixa esse método vai implementar um salvamento rápido
        //  em cache que vai ter os cover e banners (titulo/cover e titulo/banner) para validar se
        //  pode retornar corretamente e métadados também serão validados, será criado um serviço
        //  que vai chamar esse e caso o usuário faça um request do tipo post com o nome manga
        //  ao invés de retornar o json com dados ou cover/banner vai criar um comicinfo.xml
        //  e esse arquivo será salvo na pasta da obra titulo/comicinfo.xml junto aos capítulos
        //  banner e cover também serão salvos lá
        val resultMetadata = result[0]
        resultMetadata.assets.forEach { asset ->
            val file = Paths.get(PATH_FILE, asset.filename).toFile()
            file.parentFile?.mkdirs()
            file.writeBytes(asset.content)
        }

        return result[0]
    }
}