package br.mangabaka.client.singleton

import kotlinx.serialization.json.Json

object JsonConfig {
    val jsonParser: Json = Json {
        ignoreUnknownKeys = true
    }
}
