package br.mangabaka.api.controller

import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/example")
class GetMetadataManga {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getManga(@QueryParam("manga") manga: String?): Response {
        try {
            return Response.ok("response").build()
        } catch (exception: Exception) {
            System.err.println("Erro: " + exception.message)
            throw RuntimeException(exception)
        }
    }
}