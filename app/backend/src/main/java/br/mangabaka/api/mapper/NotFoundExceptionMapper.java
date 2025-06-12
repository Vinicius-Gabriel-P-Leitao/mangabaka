package br.mangabaka.api.mapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Context
    private HttpServletRequest request;

    @Override
    public Response toResponse(NotFoundException exception) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/v1")) {
            String htmlRedirect = """
                        <!DOCTYPE html>
                        <html>
                          <head>
                            <meta http-equiv="refresh" content="0;URL='/'" />
                            <script>
                              window.location.href = '/api-not-found?original=%s';
                            </script>
                          </head>
                          <body>
                            Redirecionando...
                          </body>
                        </html>
                    """.formatted(uri);

            return Response.status(Response.Status.OK)
                    .entity(htmlRedirect)
                    .type(MediaType.TEXT_HTML)
                    .build();
        }

        return Response.status(Response.Status.NOT_FOUND)
                .entity("404 Not Found")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}