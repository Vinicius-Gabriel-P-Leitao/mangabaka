package br.mangabaka.exception.throwable.base

import br.mangabaka.exception.code.ErrorCodeProvider
import br.mangabaka.infrastructure.http.anilist.dto.serializable.Status
import jakarta.ws.rs.core.Response

// @formatter:off
class InternalException(message: String, errorCode: ErrorCodeProvider,val httpError: Response.Status = Response.Status.INTERNAL_SERVER_ERROR, cause: Throwable? = null)
    : AppException(message, errorCode, cause)