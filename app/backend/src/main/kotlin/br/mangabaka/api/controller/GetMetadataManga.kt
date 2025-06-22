package br.mangabaka.api.controller

import br.mangabaka.api.dto.MangaMetadata
import br.mangabaka.service.external.anilist.FetchAnilistMangaDataService
import br.mangabaka.service.internal.MangaResolverService
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
            val fetchAnilistMangaDataService = FetchAnilistMangaDataService()
            val resolverService = MangaResolverService(listOf(fetchAnilistMangaDataService))

            if (manga != null) {
                val result: MangaMetadata = resolverService.mangaResolver(manga)
                println(result)
            }

            return Response.ok("response").build()
        } catch (exception: Exception) {
            System.err.println("Erro: " + exception.message)
            throw RuntimeException(exception)
        }
    }
}