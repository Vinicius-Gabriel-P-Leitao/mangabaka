package br.mangabaka.infrastructure.http.anilist.query

import br.mangabaka.api.dto.AssetType
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
    fun fetchAsset(url: String, mangaName: String, assetType: AssetType): DownloadedAssetDto {
        require(value = url.startsWith(prefix = "http://") || url.startsWith(prefix = "https://")) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_INVALID_URL.handle(value = "URL inválida ou não suportada: $url"),
                errorCode = AssetDownloadErrorCode.ERROR_INVALID_URL
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
            .replace(Regex(pattern = "[\\\\/:*?\"<>|]+"), replacement = "-")
            .replace(Regex(pattern = "\\s+"), replacement = "-")
            .replace(Regex(pattern = "-+"), replacement = "-")
            .trim(chars = charArrayOf('-'))

        try {
            val response: Response = client.target(url).request().get()
            if (response.status != 200) {
                response.close()
                throw AssetDownloadException(
                    message = AssetDownloadErrorCode.ERROR_CLIENT_STATUS.handle(value = "Erro ao baixar: $url (status ${response.status})"),
                    errorCode = AssetDownloadErrorCode.ERROR_CLIENT_STATUS
                )
            }

            val content: ByteArray = response.readEntity(ByteArray::class.java)
            response.close()

            return DownloadedAssetDto(
                filename = "$safeName-${assetType.code}$extension",
                mediaType = mediaType,
                content = content,
                assetType = assetType
            )
        } catch (exception: Exception) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION.handle(value = "Erro ao acessar o asset: ${exception.message}"),
                errorCode = AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION,
                cause = exception
            )
        }
    }
}