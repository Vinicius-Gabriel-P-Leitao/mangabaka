/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.query

import br.mangabaka.exception.throwable.http.GraphqlException
import br.mangabaka.infrastructure.config.graphql.GraphqlClient
import br.mangabaka.infrastructure.http.anilist.dto.anilist.MangaPaginatedDto
import jakarta.annotation.Nonnull

class MangaPaginatedQuery(
    private val client: GraphqlClient = GraphqlClient(endpoint = GRAPHQL_ENDPOINT)
) {
    companion object {
        private const val GRAPHQL_ENDPOINT = "https://graphql.anilist.co"
    }

    fun queryFactory(manga: String?, @Nonnull page: Int, @Nonnull perPage: Int): MangaPaginatedDto {
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
            "page" to page,
            "perPage" to perPage
        )

        return try {
            client.executeQuery(query, variables)
        } finally {
            client.close()
        }
    }
}