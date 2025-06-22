package br.mangabaka.api.mapper

import br.mangabaka.exception.throwable.http.AssetDownloadException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class AssetDownloaderExceptionMapper : ExceptionMapper<AssetDownloadException> {
    override fun toResponse(exception: AssetDownloadException): Response {
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