/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.config.graphql

import br.mangabaka.exception.code.http.AssetDownloadErrorCode
import br.mangabaka.exception.code.http.GraphqlErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.exception.throwable.http.GraphqlException
import br.mangabaka.infrastructure.config.singleton.JsonConfig
import jakarta.annotation.Nonnull
import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.ClientProperties
import org.glassfish.jersey.jackson.JacksonFeature

class GraphqlClient(
    val endpoint: String,
    val client: Client = ClientBuilder.newBuilder().withConfig(
        ClientConfig()
            .property(ClientProperties.CONNECT_TIMEOUT, 5000)
            .property(ClientProperties.READ_TIMEOUT, 10000)
            .register(JacksonFeature::class.java)
    ).build()
) {

    @Nonnull
    inline fun <reified T> executeQuery(query: String, variables: Map<String, Any>): T {
        require(value = endpoint.startsWith(prefix = "http://") || endpoint.startsWith(prefix = "https://")) {
            throw GraphqlException(
                message = GraphqlErrorCode.ERROR_INVALID_URL.handle(value = "URL inválida ou não suportada: $endpoint"),
                errorCode = GraphqlErrorCode.ERROR_INVALID_URL, httpError = Response.Status.BAD_GATEWAY
            )
        }

        val requestBody: Map<String, Any> = mapOf(
            "query" to query,
            "variables" to variables
        )

        return try {
            val response = client
                .target(endpoint)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON))

            if (response.status != 200) {
                throw GraphqlException(
                    message = GraphqlErrorCode.ERROR_CLIENT.handle(value = "Erro na requisição GraphQL: $response.status"),
                    errorCode = GraphqlErrorCode.ERROR_CLIENT,
                    httpError = Response.Status.BAD_GATEWAY
                )
            }

            val responseString = response.readEntity(String::class.java)
            val graphQLResponse = JsonConfig.jsonParser.decodeFromString<GraphqlResponse<T>>(responseString)
            graphQLResponse.errors?.let { errors ->
                throw GraphqlException(
                    message = GraphqlErrorCode.ERROR_CLIENT.handle(value = "Erros na resposta GraphQL: $errors"),
                    errorCode = GraphqlErrorCode.ERROR_CLIENT,
                    httpError = Response.Status.BAD_GATEWAY
                )
            }

            graphQLResponse.data ?: throw GraphqlException(
                message = GraphqlErrorCode.ERROR_EMPTY_RESPONSE.handle(value = "Resposta sem dados"),
                errorCode = GraphqlErrorCode.ERROR_EMPTY_RESPONSE,
                httpError = Response.Status.BAD_GATEWAY
            )
        } catch (processingException: ProcessingException) {
            throw GraphqlException(
                message = GraphqlErrorCode.ERROR_TIMEOUT.handle(value = "Tempo de espera para download dos métadados excedido: ${processingException.message}"),
                errorCode = GraphqlErrorCode.ERROR_TIMEOUT, httpError = Response.Status.GATEWAY_TIMEOUT
            )
        }
    }

    fun close() {
        client.close()
    }
}