/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package frontend.translation.controller

import br.mangabaka.infrastructure.config.singleton.JsonConfig
import frontend.translation.dto.I18nJsonFormat
import frontend.translation.repository.FrontendTranslationRepo
import frontend.translation.service.FrontendTranslationService
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path(value = "/frontend/translation")
class PostFrontendTranslation(
    private val repository: FrontendTranslationRepo = FrontendTranslationRepo(),
    private val service: FrontendTranslationService = FrontendTranslationService(repository)
) {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    fun createTranslation(data: String): Response {
        val i18nObject = JsonConfig.jsonParser.decodeFromString<I18nJsonFormat>(data)

        if (i18nObject.meta.language.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("error" to "Campos obrigatórios faltando"))
                .build()
        }

        service.saveTranslation(i18nObject = i18nObject)
        return Response.status(Response.Status.CREATED).entity("Salvo com sucesso!").build()
    }
}