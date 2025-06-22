package br.mangabaka.exception.code.http

import br.mangabaka.exception.code.ErrorCodeProvider

enum class GraphqlErrorCode : ErrorCodeProvider {
    ERROR_CLIENT {
        override fun handle(value: String) = "Erro no cliente Graphql: $value"
    },

    ERROR_CLIENT_STATUS {
        override fun handle(value: String) = "Erro no cliente Graphql: $value"
    },

    ERROR_EMPTY_RESPONSE {
        override fun handle(value: String) = "Erro no cliente Graphql: $value"
    }
}