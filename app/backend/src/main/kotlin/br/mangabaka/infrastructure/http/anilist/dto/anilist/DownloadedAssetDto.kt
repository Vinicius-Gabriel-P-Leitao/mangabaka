package br.mangabaka.infrastructure.http.anilist.dto.anilist

data class DownloadedAssetDto(
    val filename: String,
    val mediaType: String,
    val content: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DownloadedAssetDto

        if (filename != other.filename) return false
        if (mediaType != other.mediaType) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = filename.hashCode()
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}
