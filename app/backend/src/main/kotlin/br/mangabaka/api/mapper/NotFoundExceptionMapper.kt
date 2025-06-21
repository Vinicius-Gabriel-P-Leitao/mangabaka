package br.mangabaka.api.mapper

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider

@Provider
class NotFoundExceptionMapper : ExceptionMapper<NotFoundException> {
    @Context
    private lateinit var request: HttpServletRequest

    override fun toResponse(exception: NotFoundException): Response {
        val uri = request.requestURI

        return if (uri.startsWith("/v1")) {
            val htmlRedirect = """
                <!DOCTYPE html>
                <html>
                  <head>
                    <meta http-equiv="refresh" content="0;URL='/'" />
                    <script>
                      window.location.href = '/api-not-found?original=$uri';
                    </script>
                  </head>
                  <body>
                    Redirecionando...
                  </body>
                </html>
            """.trimIndent()

            Response.status(Response.Status.OK)
                .entity(htmlRedirect)
                .type(MediaType.TEXT_HTML)
                .build()
        } else {
            Response.status(Response.Status.NOT_FOUND)
                .entity("404 Not Found")
                .type(MediaType.TEXT_PLAIN)
                .build()
        }
    }
}