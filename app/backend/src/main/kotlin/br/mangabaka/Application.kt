package br.mangabaka

import br.mangabaka.api.controller.GetMetadataManga
import br.mangabaka.api.mapper.NotFoundExceptionMapper
import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.core.Application


@ApplicationPath("/v1")
class Application : Application() {
    override fun getClasses(): MutableSet<Class<*>?> {
        return mutableSetOf(
            GetMetadataManga::class.java,
            NotFoundExceptionMapper::class.java
        )
    }
}