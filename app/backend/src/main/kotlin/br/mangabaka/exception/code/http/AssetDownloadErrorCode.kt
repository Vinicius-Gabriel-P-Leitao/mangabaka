package br.mangabaka.exception.code.http

import br.mangabaka.exception.code.ErrorCodeProvider

enum class AssetDownloadErrorCode : ErrorCodeProvider {
    ERROR_CLIENT_STATUS {
        override fun handle(value: String) = "Erro no cliente http: $value"
    },

    ERROR_INVALID_URL {
        override fun handle(value: String) = "Url invalida: $value"
    },

    ERROR_CLIENT_EXCEPTION {
        override fun handle(value: String) = "Erro genérico lançado: $value"
    }
}