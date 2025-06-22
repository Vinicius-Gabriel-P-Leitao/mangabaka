package br.mangabaka.api.mapper

import br.mangabaka.exception.throwable.base.InternalException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class InternalExceptionMapper : ExceptionMapper<InternalException> {
    override fun toResponse(exception: InternalException): Response {
        return Response.status(exception.httpError)
            .entity(
                mapOf(
                    "error" to exception.message,
                    "code" to exception.errorCode,
                    "cause" to exception.cause?.message
                )
            )
            .build()
    }
}