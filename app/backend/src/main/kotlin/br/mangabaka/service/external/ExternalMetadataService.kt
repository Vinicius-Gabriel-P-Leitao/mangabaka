package br.mangabaka.service.external

import br.mangabaka.api.dto.MangaMetadata

interface ExternalMetadataService {
    fun fetchMangaData(mangaName: String): MangaMetadata
}