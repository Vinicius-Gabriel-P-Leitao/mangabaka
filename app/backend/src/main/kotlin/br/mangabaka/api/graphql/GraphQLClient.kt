package br.mangabaka.api.graphql

import jakarta.ws.rs.client.Client
import jakarta.ws.rs.client.ClientBuilder
import jakarta.ws.rs.client.Entity
import jakarta.ws.rs.core.GenericType
import jakarta.ws.rs.core.MediaType
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.jackson.JacksonFeature
import java.util.Map

class GraphQLClient(private val endpoint: String?) {
    private val client: Client

    init {
        val config = ClientConfig().register(JacksonFeature::class.java)
        this.client = ClientBuilder.newClient(config)
    }

    fun executeQuery(query: String, variables: MutableMap<String?, Any?>?): MutableMap<String?, Any?>? {
        val requestBody = Map.of<String?, Any?>(
            "query", query,
            "variables", variables ?: Map.of<Any?, Any?>()
        )

        val response = client.target(endpoint)
            .request(MediaType.APPLICATION_JSON)
            .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON))

        if (response.status != 200) {
            throw RuntimeException("Erro na requisição GraphQL: " + response.status)
        }

        val result: MutableMap<String, Any?> = response.readEntity(object : GenericType<MutableMap<String, Any?>>() {})

        if (result.containsKey("errors")) {
            throw RuntimeException("Erros na resposta GraphQL: " + result.get("errors"))
        }

        return result["data"] as MutableMap<String?, Any?>?
    }

    fun close() {
        client.close()
    }
}