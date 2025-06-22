package br.mangabaka.exception.throwable.http

import br.mangabaka.exception.code.ErrorCodeProvider
import br.mangabaka.exception.throwable.base.AppException

open class GraphqlException(message: String, errorCode: ErrorCodeProvider) : AppException(message, errorCode)