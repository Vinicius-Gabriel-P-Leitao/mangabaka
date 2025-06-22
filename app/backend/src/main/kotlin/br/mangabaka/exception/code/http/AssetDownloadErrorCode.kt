package br.mangabaka.exception.code.http

import br.mangabaka.exception.code.ErrorCodeProvider

enum class AssetDownloadErrorCode : ErrorCodeProvider {
    ERROR_CLIENT_STATUS {
        override fun handle(valor: String) = "Erro no cliente http: $valor"
    },

    ERROR_INVALID_URL {
        override fun handle(valor: String) = "Erro no cliente http: $valor"
    },

    ERROR_CLIENT_EXCEPTION {
        override fun handle(valor: String) = "Erro generico lançado: $valor"
    }
}