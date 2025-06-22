package br.mangabaka.api.dto

enum class AssetType(val code: String) {
    COVER("cover"), BANNER("banner");

    companion object {
        fun fromCode(code: String): AssetType? =
            AssetType.entries.find { it.code.equals(code, ignoreCase = true) }
    }
}

data class AssetInfo(
    val url: String, val mangaName: String, val type: AssetType
)