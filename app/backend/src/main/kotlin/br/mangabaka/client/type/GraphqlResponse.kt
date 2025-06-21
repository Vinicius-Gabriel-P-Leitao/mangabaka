package br.mangabaka.client.type

import kotlinx.serialization.Serializable

@Serializable
data class GraphqlResponse<T>(
    val data: T?,
    val errors: List<GraphQLError>? = null
)

@Serializable
data class GraphQLError(
    val message: String,
    val locations: List<Location>?,
    val path: List<String>?
)

@Serializable
data class Location(
    val line: Int,
    val column: Int
)