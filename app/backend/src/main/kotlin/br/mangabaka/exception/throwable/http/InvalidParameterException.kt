package br.mangabaka.exception.throwable.http

import br.mangabaka.exception.code.ErrorCodeProvider
import br.mangabaka.exception.throwable.base.AppException
import jakarta.ws.rs.core.Response

// @formatter:off
class InvalidParameterException(message: String, errorCode: ErrorCodeProvider,val httpError: Response.Status,cause: Throwable? = null) : AppException(message,errorCode,cause)