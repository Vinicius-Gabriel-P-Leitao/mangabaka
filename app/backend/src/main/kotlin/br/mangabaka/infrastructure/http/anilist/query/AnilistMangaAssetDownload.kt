/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.query

import br.mangabaka.api.dto.AssetType
import br.mangabaka.exception.code.custom.AssetDownloadErrorCode
import br.mangabaka.exception.throwable.base.AppException
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.infrastructure.http.anilist.dto.DownloadedAssetDto
import jakarta.annotation.Nonnull
import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.client.WebTarget
import jakarta.ws.rs.core.Response
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import java.net.URLConnection

class AnilistMangaAssetDownload(
    private val client: Client = ClientBuilder.newBuilder().withConfig(
        ClientConfig()
            .property(ClientProperties.CONNECT_TIMEOUT, 5000)
            .property(ClientProperties.READ_TIMEOUT, 10000)
    ).build()
) {
    @Nonnull
    fun fetchAsset(endpoint: String, mangaName: String, assetType: AssetType): DownloadedAssetDto {
        require(value = endpoint.startsWith(prefix = "http://") || endpoint.startsWith(prefix = "https://")) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_INVALID_URL.handle(value = "URL inválida ou não suportada: $endpoint"),
                errorCode = AssetDownloadErrorCode.ERROR_INVALID_URL, httpError = Response.Status.BAD_GATEWAY
            )
        }

        val mediaType: String = URLConnection.guessContentTypeFromName(endpoint) ?: "application/octet-stream"

        val extension = when {
            mediaType.contains(other = "jpeg", ignoreCase = true) -> ".jpg"
            mediaType.contains(other = "png", ignoreCase = true) -> ".png"
            else -> ".bin"
        }

        val safeName: String = mangaName
            .trim()
            .lowercase()
            .replace(Regex(pattern = "[\\\\/:*?\"<>|]+"), replacement = "-")
            .replace(Regex(pattern = "\\s+"), replacement = "-")
            .replace(Regex(pattern = "-+"), replacement = "-")
            .trim(chars = charArrayOf('-'))

        return try {
            val target: WebTarget = client.target(endpoint)
            val response: Response = target.request().get()

            if (response.status != 200) {
                response.close()
                throw AssetDownloadException(
                    message = AssetDownloadErrorCode.ERROR_CLIENT_STATUS.handle(value = "Erro ao baixar: $endpoint (status ${response.status})"),
                    errorCode = AssetDownloadErrorCode.ERROR_CLIENT_STATUS, httpError = Response.Status.BAD_GATEWAY
                )
            }

            val content: ByteArray = response.readEntity(ByteArray::class.java)

            DownloadedAssetDto(
                filename = "$safeName-${assetType.code}$extension",
                mediaType = mediaType,
                content = content,
                assetType = assetType
            )
        } catch (processingException: ProcessingException) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_TIMEOUT.handle(value = "Tempo de espera para download do asset excedido: ${processingException.message}"),
                errorCode = AssetDownloadErrorCode.ERROR_TIMEOUT,
                cause = processingException, httpError = Response.Status.GATEWAY_TIMEOUT
            )
        } catch (exception: Exception) {
            when (exception) {
                is AppException -> throw exception
                else -> throw AssetDownloadException(
                    message = AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION.handle(value = "Erro ao acessar o asset: ${exception.message}"),
                    errorCode = AssetDownloadErrorCode.ERROR_CLIENT_EXCEPTION,
                    cause = exception, httpError = Response.Status.BAD_GATEWAY
                )
            }
        }
    }

    fun close() {
        client.close()
    }
}