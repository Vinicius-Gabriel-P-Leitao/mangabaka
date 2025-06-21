package br.mangabaka.client.graphql

import br.mangabaka.client.singleton.JsonConfig
import br.mangabaka.client.type.GraphqlResponse
import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.core.MediaType
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.jackson.JacksonFeature

class GraphQLClient(val endpoint: String) {
    val client: Client

    init {
        val config = ClientConfig().register(JacksonFeature::class.java)
        this.client = ClientBuilder.newClient(config)
    }

    inline fun <reified T> executeQuery(query: String, variables: Map<String, Any>): T {
        val requestBody: Map<String, Any> = mapOf(
            "query" to query, "variables" to variables
        )

        val response = client.target(endpoint).request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON))

        if (response.status != 200) {
            throw RuntimeException("Erro na requisição GraphQL: " + response.status)
        }

        val responseString = response.readEntity(String::class.java)
        val graphQLResponse = JsonConfig.jsonParser.decodeFromString<GraphqlResponse<T>>(responseString)

        graphQLResponse.errors?.let { errors ->
            throw RuntimeException("Erros na resposta GraphQL: $errors")
        }

        return graphQLResponse.data ?: throw RuntimeException("Resposta sem dados")
    }

    fun close() {
        client.close()
    }
}