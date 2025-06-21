package br.mangabaka.service.external.anilist

import br.mangabaka.api.dto.MangaMetadata
import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
import br.mangabaka.infrastructure.http.anilist.dto.anilist.MangaPaginatedDto
import br.mangabaka.infrastructure.http.anilist.query.MangaAssetDownload
import br.mangabaka.infrastructure.http.anilist.query.MangaPaginatedQuery
import br.mangabaka.service.external.ExternalMetadataService
import kotlinx.serialization.json.internal.encodeByWriter

class AnilistMangaMetadataService : ExternalMetadataService {
    companion object {
        private const val PAGE: Int = 1
        private const val PER_PAGE: Int = 1
    }

    data class AssetInfo(
        val url: String,
        val mangaName: String,
        val type: String
    )

    override fun fetchMetadata(mangaName: String): MangaMetadata {
        val mangaMetadata: MangaPaginatedDto = MangaPaginatedQuery().queryFactory(mangaName, PAGE, PER_PAGE)

        val maxAssets = PER_PAGE * 2
        val assetList = arrayOfNulls<AssetInfo>(maxAssets)

        var index = 0
        for (value in mangaMetadata.page.media) {
            val mangaName = value.title.english ?: value.title.romaji ?: value.title.native ?: "unknown"

            val coverUrl: String? = value.coverImage.large
            val bannerUrl: String? = value.bannerImage

            if (coverUrl != null && index <  maxAssets) {
                assetList[index] = AssetInfo(coverUrl, mangaName, "cover")
                index++
            }

            if (bannerUrl != null && index <  maxAssets) {
                assetList[index] = AssetInfo(bannerUrl, mangaName, "banner")
                index++
            }
        }

        for (assetInfo in assetList.filterNotNull()) {
            val mangaAsset: DownloadedAssetDto = MangaAssetDownload().fetchAsset(assetInfo.url, assetInfo.mangaName, assetInfo.type)
            print(mangaAsset.content)
            print(mangaAsset.filename)
            print(mangaAsset.mediaType)
        }

        return MangaMetadata(1)
    }
}