package br.mangabaka.infrastructure.config.singleton

import kotlinx.serialization.json.Json

object JsonConfig {
    val jsonParser: Json = Json {
        ignoreUnknownKeys = true
    }
}
