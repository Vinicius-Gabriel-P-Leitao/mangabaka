package br.mangabaka.api.dto

import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
import br.mangabaka.infrastructure.http.anilist.dto.anilist.MangaPaginatedDto

data class MangaMetadata(
    val paginationInfo: MangaPaginatedDto,
    val assets: List<DownloadedAssetDto>
)
