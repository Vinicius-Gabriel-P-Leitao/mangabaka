/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.infrastructure.config.singleton

object AppConfig {
    val backendMode: BackendMode = BackendMode.fromEnv(System.getenv("BACKEND_MODE"))
}

enum class BackendMode {
    ALL, API;

    companion object {
        fun fromEnv(envValue: String?): BackendMode = when (envValue?.uppercase()) {
            "ALL" -> ALL
            "API" -> API
            else -> ALL
        }
    }
}