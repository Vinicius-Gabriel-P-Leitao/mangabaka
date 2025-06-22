package br.mangabaka.service.external.anilist

import br.mangabaka.api.dto.AssetInfo
import br.mangabaka.exception.code.http.AssetDownloadErrorCode
import br.mangabaka.exception.throwable.http.AssetDownloadException
import br.mangabaka.infrastructure.http.anilist.dto.anilist.DownloadedAssetDto
import br.mangabaka.infrastructure.http.anilist.query.MangaAssetDownload
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class FetchAnilistMangaAssetService {
    companion object {
        private val logger: Logger = LogManager.getLogger(FetchAnilistMangaAssetService::class.java)
    }

    fun mangaAsset(assetListUrl: Array<AssetInfo?>): List<DownloadedAssetDto> {
        if (assetListUrl.isEmpty()) {
            throw AssetDownloadException(
                message = AssetDownloadErrorCode.ERROR_INVALID_URL.handle(value = "A lista de URL para cover e banner está vázia"),
                errorCode = AssetDownloadErrorCode.ERROR_INVALID_URL
            )
        }

        val mangaAssets = mutableListOf<DownloadedAssetDto>()
        assetListUrl.filterNotNull().forEach { assetInfo ->
            try {
                val mangaAsset = MangaAssetDownload().fetchAsset(
                    url = assetInfo.url, mangaName = assetInfo.mangaName, assetType = assetInfo.type
                )
                logger.trace(mangaAsset.filename)
                logger.trace(mangaAsset.mediaType)

                mangaAssets.add(mangaAsset)
            } catch (exception: AssetDownloadException) {
                throw exception
            }
        }

        return mangaAssets;
    }
}