package br.mangabaka.infrastructure.http.anilist.query

import br.mangabaka.exception.code.http.AssetDownloadErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
import jakarta.annotation.Nonnull
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.core.Response
import java.net.URLConnection
import java.util.concurrent.TimeUnit

class MangaAssetDownload {
    private val client: Client = ClientBuilder.newBuilder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    @Nonnull
    fun fetchAsset(url: String, mangaName: String, type: String): DownloadedAssetDto {
        require(url.startsWith("http://") || url.startsWith("https://")) {
            throw AssetDownloadException(
                AssetDownloadErrorCode.ERROR_INVALID_URL.handle("URL inválida ou não suportada: $url"),
                AssetDownloadErrorCode.ERROR_INVALID_URL
            )
        }

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

        try {
            val response: Response = client.target(url).request().get()
            if (response.status != 200) {
                response.close()
                throw AssetDownloadException(
                    AssetDownloadErrorCode.ERROR_CLIENT_STATUS.handle("Erro ao baixar: $url (status ${response.status})"),
                    AssetDownloadErrorCode.ERROR_CLIENT_STATUS
                )
            }

            val content: ByteArray = response.readEntity(ByteArray::class.java)
            response.close()

            return DownloadedAssetDto(
                filename = fileName,
                mediaType = mediaType,
                content = content
            )
        } catch (exception: Exception) {
            throw AssetDownloadException(
                AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION.handle("Erro ao acessar o asset: ${exception.message}"),
                AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION,
                exception
            )
        }
    }
}