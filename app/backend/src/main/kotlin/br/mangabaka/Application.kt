/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka

import br.mangabaka.api.controller.GetDataManga
import br.mangabaka.api.mapper.custom.*
import br.mangabaka.api.mapper.jersey.BadRequestExceptionMapper
import br.mangabaka.api.mapper.jersey.NotFoundExceptionMapper
import br.mangabaka.infrastructure.config.AppConfig
import br.mangabaka.infrastructure.config.BackendMode
import br.mangabaka.infrastructure.config.database.PostgresqlConfig
import jakarta.ws.rs.ApplicationPath
import jakarta.ws.rs.core.Application

@ApplicationPath("/v1")
class Application : Application() {
    override fun getClasses(): MutableSet<Class<*>?> {
        val classes: MutableSet<Class<*>?> = mutableSetOf(
            // NOTE: Classes de controller
            GetDataManga::class.java,
            // NOTE: Mappers do jersey
            NotFoundExceptionMapper::class.java,
            BadRequestExceptionMapper::class.java,
            // NOTE: Mappers custom
            GraphqlExceptionMapper::class.java,
            MetadataExceptionMapper::class.java,
            InvalidParameterExceptionMapper::class.java,
            InternalExceptionMapper::class.java,
            AssetDownloadExceptionMapper::class.java,
        )

        PostgresqlConfig().configure();

        return when (AppConfig.backendMode) {
            BackendMode.ALL -> {
                try {
                    val clazz = Class.forName("frontend.translation.controller.PostFrontendTranslation")
                    classes.add(clazz)

                    classes
                } catch (exception: ClassNotFoundException) {
                    throw exception // TODO: Tratar esse erro de forma agradável
                }
            }

            BackendMode.API -> classes

        }
    }
}