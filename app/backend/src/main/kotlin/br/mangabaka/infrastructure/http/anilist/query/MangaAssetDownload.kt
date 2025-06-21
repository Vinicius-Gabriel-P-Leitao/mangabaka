package br.mangabaka.infrastructure.http.anilist.query

import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.core.Response
import java.net.URLConnection

class MangaAssetDownload {
    private val client: Client = ClientBuilder.newBuilder().build()

    fun fetchAsset(url: String, mangaName: String, type: String): DownloadedAssetDto {
        val mediaType: String = URLConnection.guessContentTypeFromName(url) ?: "application/octet-stream"

        val extension: String = when (mediaType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            else -> ".bin"
        }

        val safeName: String = mangaName
            .trim()
            .lowercase()
            .replace(Regex("[\\\\/:*?\"<>|]+"), "-")
            .replace(Regex("\\s+"), "-")
            .replace(Regex("-+"), "-")
            .trim('-')

        val fileName = "$safeName-$type$extension"

        val response: Response = client.target(url).request().get()
        if (response.status != 200) {
            response.close()
            throw RuntimeException("Erro ao baixar: $url (status ${response.status})")
        }

        val content: ByteArray = response.readEntity(ByteArray::class.java)
        response.close()

        return DownloadedAssetDto(
            filename = fileName,
            mediaType = mediaType,
            content = content
        )
    }
}