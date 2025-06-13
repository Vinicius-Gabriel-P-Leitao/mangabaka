package br.mangabaka.api.graphql;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import java.util.Map;

public class GraphQLClient {
    private final Client client;
    private final String endpoint;

    public GraphQLClient(String endpoint) {
        this.endpoint = endpoint;
        ClientConfig config = new ClientConfig().register(JacksonFeature.class);
        this.client = ClientBuilder.newClient(config);
    }

    public Map<String, Object> executeQuery(String query, Map<String, Object> variables) {
        Map<String, Object> requestBody = Map.of(
                "query", query,
                "variables", variables != null ? variables : Map.of()
        );

        Response response = client.target(endpoint)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

        if (response.getStatus() != 200) {
            throw new RuntimeException("Erro na requisição GraphQL: " + response.getStatus());
        }

        Map<String, Object> result = response.readEntity(Map.class);

        if (result.containsKey("errors")) {
            throw new RuntimeException("Erros na resposta GraphQL: " + result.get("errors"));
        }

        return (Map<String, Object>) result.get("data");
    }

    public void close() {
        client.close();
    }
}