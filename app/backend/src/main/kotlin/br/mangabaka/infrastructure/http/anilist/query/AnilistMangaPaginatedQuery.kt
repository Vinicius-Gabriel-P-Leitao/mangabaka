/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.http.anilist.query

import br.mangabaka.infrastructure.config.graphql.GraphqlClient
import br.mangabaka.infrastructure.http.anilist.dto.MangaPaginatedAssetsDto
import br.mangabaka.infrastructure.http.anilist.dto.MangaPaginatedMetadataDto
import jakarta.annotation.Nonnull

class AnilistMangaPaginatedQuery(
    private val client: GraphqlClient = GraphqlClient(endpoint = GRAPHQL_ENDPOINT)
) {
    companion object {
        private const val GRAPHQL_ENDPOINT = "https://graphql.anilist.co"
    }

    fun queryAllDataFactory(manga: String?, @Nonnull page: Int, @Nonnull perPage: Int): MangaPaginatedMetadataDto {
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

    fun queryAssetDataFactory(manga: String?, @Nonnull page: Int, @Nonnull perPage: Int): MangaPaginatedAssetsDto {
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
                                title {
                                    romaji
                                    english
                                    native
                                }
                                coverImage {
                                    large
                                }
                                bannerImage
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