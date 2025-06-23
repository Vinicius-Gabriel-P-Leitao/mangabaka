/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

package br.mangabaka.service.external.anilist

import br.mangabaka.api.dto.MangaDataDto
import br.mangabaka.exception.code.http.MetadataErrorCode
import br.mangabaka.exception.throwable.http.MetadataException
import br.mangabaka.infrastructure.http.anilist.dto.MangaPaginatedMetadataDto
import br.mangabaka.infrastructure.http.anilist.query.AnilistMangaPaginatedQuery
import br.mangabaka.service.external.ExternalMetadataService
import br.mangabaka.service.internal.MangaResolverService.Companion.PAGE
import br.mangabaka.service.internal.MangaResolverService.Companion.PER_PAGE
import jakarta.ws.rs.core.Response
import kotlinx.serialization.SerializationException

class FetchAnilistMangaDataService(
    private val query: AnilistMangaPaginatedQuery = AnilistMangaPaginatedQuery(),
    ) : ExternalMetadataService {
        override fun fetchMangaData(mangaName: String): MangaDataDto {
            try {
                val mangaMetadata: MangaPaginatedMetadataDto =
                    query.queryAllDataFactory(manga = mangaName, page = PAGE, perPage = PER_PAGE)

                if (mangaMetadata.page.media.isEmpty()) {
                    throw MetadataException(
                        message = MetadataErrorCode.ERROR_FIELD_EMPTY.handle(value = "Nenhuma media foi encontrada para o manga: $mangaName"),
                        errorCode = MetadataErrorCode.ERROR_FIELD_EMPTY,
                        httpError = Response.Status.NOT_FOUND
                    )
                }

                return MangaDataDto(paginationInfo = mangaMetadata, assets = null)
            } catch (exception: SerializationException) {
                throw MetadataException(
                    message = MetadataErrorCode.ERROR_JSON_MALFORMED.handle(value = "Nenhuma media foi encontrada para o manga: ${exception.message}"),
                    errorCode = MetadataErrorCode.ERROR_JSON_MALFORMED,
                    httpError = Response.Status.NOT_FOUND
                )
            }
        }
    }