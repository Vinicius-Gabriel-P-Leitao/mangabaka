package br.mangabaka.api.mapper

import br.mangabaka.exception.throwable.http.GraphqlException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class GraphqlExceptionMapper : ExceptionMapper<GraphqlException> {
    override fun toResponse(exception: GraphqlException): Response {
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(
                mapOf(
                    "error" to exception.message,
                    "code" to exception.errorCode
                )
            )
            .build()
    }
}