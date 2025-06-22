package br.mangabaka.exception.code.http

import br.mangabaka.exception.code.ErrorCodeProvider

enum class InternalErrorCode : ErrorCodeProvider {
    ERROR_INTERNAL_GENERIC {
        override fun handle(value: String) = "Erro interno de servidor: $value"
    },
}