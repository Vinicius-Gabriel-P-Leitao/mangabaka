/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.config.graphql

import br.mangabaka.api.dto.AssetType
import br.mangabaka.exception.code.http.AssetDownloadErrorCode
import br.mangabaka.exception.code.http.GraphqlErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.exception.throwable.http.GraphqlException
import br.mangabaka.infrastructure.http.anilist.query.AnilistMangaAssetDownload
import jakarta.ws.rs.ProcessingException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.client.Invocation
import jakarta.ws.rs.client.WebTarget
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever

class GraphqlClientTest {
    private val mockClient = mock<Client>()
    private val mockTarget = mock<WebTarget>()
    private val mockInvocationBuilder = mock<Invocation.Builder>()
    private val mockResponse = mock<Response>()

    private val endpoint = "https://fakeendpoint/graphql"
    private val query = "{ testQuery }"
    private val variables = emptyMap<String, Any>()

    @BeforeEach
    fun setup() {
        reset(mockClient, mockTarget, mockInvocationBuilder, mockResponse)
    }

    @Test
    fun `test executeQuery successful response`() {
        whenever(methodCall = mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(methodCall = mockTarget.request(MediaType.APPLICATION_JSON)).thenReturn(mockInvocationBuilder)
        whenever(methodCall = mockInvocationBuilder.post(any<Entity<Map<String, Any>>>())).thenReturn(mockResponse)
        whenever(methodCall = mockResponse.status).thenReturn(200)
        whenever(methodCall = mockResponse.readEntity(String::class.java)).thenReturn(
            """
                {
                   "data": {
                        "field": "value"
                        },
                    "errors": null
                }""".trimMargin()
        )

        val graphqlClient = GraphqlClient(endpoint, mockClient)

        val result: Map<String, String> = graphqlClient.executeQuery(query, variables)
        assert(result["field"] == "value")
    }

    @Test
    fun `test executeQuery throws exception on error client status`() {
        whenever(methodCall = mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(methodCall = mockTarget.request(MediaType.APPLICATION_JSON)).thenReturn(mockInvocationBuilder)
        whenever(methodCall = mockInvocationBuilder.post(any<Entity<Map<String, Any>>>())).thenReturn(mockResponse)
        whenever(methodCall = mockResponse.status).thenReturn(500)

        val graphqlClient = GraphqlClient(endpoint, mockClient)

        try {
            graphqlClient.executeQuery<Map<String, String>>(query, variables)
            assert(false) { "Expected GraphqlException" }
        } catch (exception: GraphqlException) {
            assert(exception.errorCode == GraphqlErrorCode.ERROR_CLIENT)
        }
    }

    @Test
    fun `test executeQuery throws exception on error client`() {
        whenever(methodCall = mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(methodCall = mockTarget.request(MediaType.APPLICATION_JSON)).thenReturn(mockInvocationBuilder)
        whenever(methodCall = mockInvocationBuilder.post(any<Entity<Map<String, Any>>>())).thenReturn(mockResponse)
        whenever(methodCall = mockResponse.status).thenReturn(200)
        whenever(methodCall = mockResponse.readEntity(String::class.java)).thenReturn(
            """
                { 
                    "data": null, 
                    "errors": [
                         {
                            "message": "Algo deu errado",
                            "locations": null,
                            "path": null
                        }
                    ] 
                }""".trimIndent()
        )

        val graphqlClient = GraphqlClient(endpoint, mockClient)

        val exception = assertThrows<GraphqlException> {
            graphqlClient.executeQuery<Map<String, String>>(query, variables)
        }

        assertEquals(GraphqlErrorCode.ERROR_CLIENT, exception.errorCode)
    }

    @Test
    fun `test executeQuery throws exception on error empty data`() {
        whenever(methodCall = mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(methodCall = mockTarget.request(MediaType.APPLICATION_JSON)).thenReturn(mockInvocationBuilder)
        whenever(methodCall = mockInvocationBuilder.post(any<Entity<Map<String, Any>>>())).thenReturn(mockResponse)
        whenever(methodCall = mockResponse.status).thenReturn(200)
        whenever(methodCall = mockResponse.readEntity(String::class.java)).thenReturn(
            """
                {
                    "data": null
                    "errors": null
                }""".trimIndent()
        )

        val graphqlClient = GraphqlClient(endpoint, mockClient)

        val exception = assertThrows<GraphqlException> {
            graphqlClient.executeQuery<Map<String, String>>(query, mapOf())
        }

        assertEquals(GraphqlErrorCode.ERROR_EMPTY_RESPONSE, exception.errorCode)
    }

    @Test
    fun `test post throws exception ProcessingException on timeout`() {
        whenever(methodCall = mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(methodCall = mockTarget.request(MediaType.APPLICATION_JSON)).thenReturn(mockInvocationBuilder)
        whenever(methodCall = mockInvocationBuilder.post(any<Entity<Map<String, Any>>>())).thenThrow(
            ProcessingException("timeout")
        )

        val graphqlClient = GraphqlClient(endpoint, mockClient)

        val exception = assertThrows<GraphqlException> {
            graphqlClient.executeQuery<Map<String, String>>(query, variables)
        }

        assertEquals(GraphqlErrorCode.ERROR_TIMEOUT, exception.errorCode)
    }

    @Test
    fun `test executeQuery throws exception on invalid endpoint`() {
        val graphqlClient = GraphqlClient("invalid endpoint", mockClient)

        val exception = assertThrows<GraphqlException> {
            graphqlClient.executeQuery<Map<String, String>>(query, variables)
        }

        assertEquals(GraphqlErrorCode.ERROR_INVALID_URL, exception.errorCode)
    }
}