package br.mangabaka.exception.throwable.base

import br.mangabaka.exception.code.ErrorCodeProvider

open class AppException(message: String, val errorCode: ErrorCodeProvider) : IllegalStateException(message) {
    override fun toString(): String {
        return "AppException(errorCode=${errorCode.javaClass.simpleName}, message=$message)"
    }
}