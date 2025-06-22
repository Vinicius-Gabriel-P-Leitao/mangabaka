package br.mangabaka.infrastructure.config.graphql

import br.mangabaka.exception.code.http.GraphqlErrorCode
import br.mangabaka.exception.throwable.http.GraphqlException
import br.mangabaka.infrastructure.config.singleton.JsonConfig
import jakarta.annotation.Nonnull
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.core.MediaType
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.jackson.JacksonFeature

class GraphqlClient(
    val endpoint: String,
    val client: Client = ClientBuilder.newClient(
        ClientConfig().register(JacksonFeature::class.java)
    )
) {

    @Nonnull
    inline fun <reified T> executeQuery(query: String, variables: Map<String, Any>): T {
        val requestBody: Map<String, Any> = mapOf(
            "query" to query, "variables" to variables
        )

        val response = client.target(endpoint).request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON))

        if (response.status != 200) {
            throw GraphqlException(
                GraphqlErrorCode.ERROR_CLIENT.handle("Erro na requisição GraphQL: $response.status"),
                GraphqlErrorCode.ERROR_CLIENT_STATUS
            )
        }

        val responseString = response.readEntity(String::class.java)
        val graphQLResponse = JsonConfig.jsonParser.decodeFromString<GraphqlResponse<T>>(responseString)
        graphQLResponse.errors?.let { errors ->
            throw GraphqlException(
                GraphqlErrorCode.ERROR_CLIENT.handle("Erros na resposta GraphQL: $errors"),
                GraphqlErrorCode.ERROR_CLIENT
            )
        }

        return graphQLResponse.data ?: throw GraphqlException(
            GraphqlErrorCode.ERROR_CLIENT.handle("Resposta sem dados"), GraphqlErrorCode.ERROR_EMPTY_RESPONSE
        )
    }

    fun close() {
        client.close()
    }
}