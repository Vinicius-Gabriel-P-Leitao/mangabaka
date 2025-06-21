package br.mangabaka.exception.code.http

import br.mangabaka.exception.code.ErrorCodeProvider

enum class GraphqlErrorCode : ErrorCodeProvider {
    ERROR_CLIENT {
        override fun handle(valor: String) = "Erro no cliente Graphql: $valor"
    },

    ERROR_CLIENT_STATUS {
        override fun handle(valor: String) = "Erro no cliente Graphql: $valor"
    },

    ERROR_EMPTY_RESPONSE {
        override fun handle(valor: String) = "Erro no cliente Graphql: $valor"
    }
}