package br.mangabaka.service.internal

import br.mangabaka.api.dto.MangaMetadata
import br.mangabaka.service.external.ExternalMetadataService

class MangaResolverService(private val services: List<ExternalMetadataService>) {
    fun mangaResolver(mangaName: String): MangaMetadata {
        val resultados: List<MangaMetadata> = services.map { it.fetchMangaData(mangaName) }
        return resultados[0]
    }
}