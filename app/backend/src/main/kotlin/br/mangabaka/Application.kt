package br.mangabaka

import br.mangabaka.api.controller.GetDataManga
import br.mangabaka.api.mapper.GraphqlExceptionMapper
import br.mangabaka.api.mapper.NotFoundExceptionMapper
import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.core.Application


@ApplicationPath("/v1")
class Application : Application() {
    override fun getClasses(): MutableSet<Class<*>?> {
        return mutableSetOf(
            // NOTE: Classes de controller
            GetDataManga::class.java,

            // NOTE: Classes de mapper
            NotFoundExceptionMapper::class.java,
            GraphqlExceptionMapper::class.java,
        )
    }
}