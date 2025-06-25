/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.infrastructure.config.graphql

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