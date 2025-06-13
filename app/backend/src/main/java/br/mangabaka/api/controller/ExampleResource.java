package br.mangabaka.api.controller;

import br.mangabaka.api.graphql.GraphQLClient;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

@Path("/example")
public class ExampleResource {
    private static final String GRAPHQL_ENDPOINT = "https://graphql.anilist.co";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getExample() {
        GraphQLClient client = new GraphQLClient(GRAPHQL_ENDPOINT);

        String query = """
                query ($id: Int, $page: Int, $perPage: Int, $search: String) {
                    Page(page: $page, perPage: $perPage) {
                        pageInfo {
                            currentPage
                            hasNextPage
                            perPage
                        }
                        media(id: $id, search: $search) {
                            id
                            title {
                                romaji
                                english
                                native
                            }
                        }
                    }
                }
                """;

        Map<String, Object> variables = Map.of("search", "Fate/Zero", "page", "1", "perPage", "3");

        try {
            Map<String, Object> response = client.executeQuery(query, variables);
            return Response.ok(response).build();
        } catch (Exception exception) {
            System.err.println("Erro: " + exception.getMessage());
            throw new RuntimeException(exception);
        } finally {
            client.close();
        }
    }
}