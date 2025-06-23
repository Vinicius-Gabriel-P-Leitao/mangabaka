/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka

import br.mangabaka.api.controller.GetDataManga
import br.mangabaka.api.mapper.AssetDownloadExceptionMapper
import br.mangabaka.api.mapper.GraphqlExceptionMapper
import br.mangabaka.api.mapper.InternalExceptionMapper
import br.mangabaka.api.mapper.InvalidParameterExceptionMapper
import br.mangabaka.api.mapper.MetadataExceptionMapper
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
            MetadataExceptionMapper::class.java,
            InvalidParameterExceptionMapper::class.java,
            InternalExceptionMapper::class.java,
            AssetDownloadExceptionMapper::class.java,
        )
    }
}