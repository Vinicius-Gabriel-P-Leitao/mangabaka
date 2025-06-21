package br.mangabaka.api.controller

import br.mangabaka.api.graphql.GraphQLClient
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.util.Map

@Path("/example")
class ExampleResource {
    companion object {
        private const val GRAPHQL_ENDPOINT = "https://graphql.anilist.co"
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun example(): Response {
        val client = GraphQLClient(GRAPHQL_ENDPOINT)

        val query = """
                    query (${'$'}id: Int, ${'$'}page: Int, ${'$'}perPage: Int, ${'$'}search: String) {
                        Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                            pageInfo {
                                currentPage
                                hasNextPage
                                perPage
                            }
                            
                            media(id: ${'$'}id, search: ${'$'}search) {
                                id
                                title {
                                    romaji
                                    english
                                    native
                                }
                            }
                        }
                    }
                    
                    """.trimIndent()

        val variables = Map.of<String?, Any?>("search", "Fate/Zero", "page", "1", "perPage", "3")

        try {
            val response = client.executeQuery(query, variables)
            return Response.ok(response).build()
        } catch (exception: Exception) {
            System.err.println("Erro: " + exception.message)
            throw RuntimeException(exception)
        } finally {
            client.close()
        }
    }
}