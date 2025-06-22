package br.mangabaka.infrastructure.config.graphql

import br.mangabaka.exception.code.http.GraphqlErrorCode
import br.mangabaka.exception.throwable.http.GraphqlException
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.client.Invocation
import jakarta.ws.rs.client.WebTarget
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GraphqlClientTest {
    private val mockClient = mock<Client>()
    private val mockTarget = mock<WebTarget>()
    private val mockInvocationBuilder = mock<Invocation.Builder>()
    private val mockResponse = mock<Response>()

    private val endpoint = "https://fakeendpoint/graphql"
    private val query = "{ testQuery }"
    private val variables = emptyMap<String, Any>()

    @Test
    fun `test executeQuery successful response`() {
        // Setup do encadeamento de mocks
        whenever(mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(mockTarget.request(MediaType.APPLICATION_JSON)).thenReturn(mockInvocationBuilder)
        whenever(mockInvocationBuilder.post(any<Entity<Map<String, Any>>>())).thenReturn(mockResponse)

        whenever(mockResponse.status).thenReturn(200)
        whenever(mockResponse.readEntity(String::class.java)).thenReturn(
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
        whenever(mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(mockTarget.request(MediaType.APPLICATION_JSON)).thenReturn(mockInvocationBuilder)
        whenever(mockInvocationBuilder.post(any<Entity<Map<String, Any>>>())).thenReturn(mockResponse)

        whenever(mockResponse.status).thenReturn(500)

        val graphqlClient = GraphqlClient(endpoint, mockClient)

        try {
            graphqlClient.executeQuery<Map<String, String>>(query, variables)
            assert(false) { "Esperava GraphqlException" }
        } catch (exception: GraphqlException) {
            assert(exception.errorCode == GraphqlErrorCode.ERROR_CLIENT_STATUS)
        }
    }

    @Test
    fun `test executeQuery throws exception on error client`() {
        whenever(mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(mockTarget.request(MediaType.APPLICATION_JSON)).thenReturn(mockInvocationBuilder)
        whenever(mockInvocationBuilder.post(any<Entity<Map<String, Any>>>())).thenReturn(mockResponse)
        whenever(mockResponse.status).thenReturn(200)

        val jsonComErros = """
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

        whenever(mockResponse.readEntity(String::class.java)).thenReturn(jsonComErros)

        val graphqlClient = GraphqlClient(endpoint, mockClient)

        val exception = assertThrows<GraphqlException> {
            graphqlClient.executeQuery<Map<String, String>>(query, variables)
        }

        assertEquals(GraphqlErrorCode.ERROR_CLIENT, exception.errorCode)
    }

    @Test
    fun `test executeQuery throws exception on error empty data`() {
        whenever(mockClient.target(endpoint)).thenReturn(mockTarget)
        whenever(mockTarget.request(MediaType.APPLICATION_JSON)).thenReturn(mockInvocationBuilder)
        whenever(mockInvocationBuilder.post(any<Entity<Map<String, Any>>>())).thenReturn(mockResponse)
        whenever(mockResponse.status).thenReturn(200)

        val jsonSemData = """
        {
            "data": null
            "errors": null
        }""".trimIndent()

        whenever(mockResponse.readEntity(String::class.java)).thenReturn(jsonSemData)

        val graphqlClient = GraphqlClient(endpoint, mockClient)

        val exception = assertThrows<GraphqlException> {
            graphqlClient.executeQuery<Map<String, String>>(query, mapOf())
        }

        assertEquals(GraphqlErrorCode.ERROR_EMPTY_RESPONSE, exception.errorCode)
    }
}