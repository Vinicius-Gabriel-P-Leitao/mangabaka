/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.query

import br.mangabaka.api.dto.AssetType
import br.mangabaka.exception.code.http.AssetDownloadErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
import jakarta.annotation.Nonnull
import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.core.Response
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import java.net.URLConnection

class MangaAssetDownload(
    private val client: Client = ClientBuilder.newBuilder().withConfig(
        ClientConfig()
            .property(ClientProperties.CONNECT_TIMEOUT, 5000)
            .property(ClientProperties.READ_TIMEOUT, 10000)
    ).build()
) {
    @Nonnull
    fun fetchAsset(url: String, mangaName: String, assetType: AssetType): DownloadedAssetDto {
        require(value = url.startsWith(prefix = "http://") || url.startsWith(prefix = "https://")) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_INVALID_URL.handle(value = "URL inválida ou não suportada: $url"),
                errorCode = AssetDownloadErrorCode.ERROR_INVALID_URL, httpError = Response.Status.BAD_GATEWAY
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
                    errorCode = AssetDownloadErrorCode.ERROR_CLIENT_STATUS, httpError = Response.Status.BAD_GATEWAY
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
        } catch (processingException: ProcessingException) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION.handle(value = "Tempo de espera para download do asset excedido: ${processingException.message}"),
                errorCode = AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION,
                cause = processingException, httpError = Response.Status.GATEWAY_TIMEOUT
            )
        } catch (exception: Exception) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION.handle(value = "Erro ao acessar o asset: ${exception.message}"),
                errorCode = AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION,
                cause = exception, httpError = Response.Status.BAD_GATEWAY
            )
        }
    }
}