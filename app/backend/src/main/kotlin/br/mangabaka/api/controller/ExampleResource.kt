package br.mangabaka.api.controller

import br.mangabaka.client.graphql.GraphQLClient
import br.mangabaka.client.type.anilist.MangaPaginatedQuery
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/example")
class ExampleResource {
    companion object {
        private const val GRAPHQL_ENDPOINT = "https://graphql.anilist.co"
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun example(@QueryParam("manga") manga: String?): Response {
        val client = GraphQLClient(GRAPHQL_ENDPOINT)

        val query = """
                    query (${'$'}id: Int, ${'$'}page: Int, ${'$'}perPage: Int, ${'$'}search: String) {
                        Page(page: ${'$'}page, perPage: ${'$'}perPage) {
                            pageInfo {
                                currentPage
                                hasNextPage
                                perPage
                            }
                            media(id: ${'$'}id, search: ${'$'}search, sort: POPULARITY_DESC, type: MANGA) {
                                id
                                idMal
                                status
                                chapters
                                volumes
                                isAdult
                                averageScore
                                countryOfOrigin
                                format
                                startDate {
                                    year
                                    month
                                    day
                                }
                                endDate {
                                    year
                                    month
                                    day
                                }
                                title {
                                    romaji
                                    english
                                    native
                                }
                                synonyms
                                description(asHtml: false)
                                genres
                                tags {
                                    name
                                    rank
                                }
                                coverImage {
                                    large
                                    color
                                }
                                bannerImage
                                siteUrl
                            }
                        }
                    }
                    """.trimIndent()

        val variables: Map<String, Any> = mapOf(
            "search" to manga.orEmpty(),
            "page" to 1,
            "perPage" to 3
        )

        try {
            val response: MangaPaginatedQuery = client.executeQuery(query, variables)
            return Response.ok(response).build()
        } catch (exception: Exception) {
            System.err.println("Erro: " + exception.message)
            throw RuntimeException(exception)
        } finally {
            client.close()
        }
    }
}