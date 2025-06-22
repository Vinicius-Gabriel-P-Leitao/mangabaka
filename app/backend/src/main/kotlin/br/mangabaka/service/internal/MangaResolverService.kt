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

        val resultMetadata = result[0]
        resultMetadata.assets.forEach { asset ->
            val file = Paths.get(PATH_FILE, asset.filename).toFile()
            file.parentFile?.mkdirs()
            file.writeBytes(asset.content)
        }

        return result[0]
    }
}