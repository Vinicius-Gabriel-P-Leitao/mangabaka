package br.mangabaka.exception.code.http

import br.mangabaka.exception.code.ErrorCodeProvider

enum class InvalidParameterErrorCode : ErrorCodeProvider {
    ERROR_PARAMETER_EMPTY {
        override fun handle(value: String) = "Erro de parametro vazio: $value"
    },

    ERROR_PARAMETER_INVALID {
        override fun handle(value: String) = "O parametro é invalido: $value"
    },
}