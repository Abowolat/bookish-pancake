package com.kobani.assetfetcher

/**
 * AssetsConfig - Configuration for assets to be fetched
 * This provides a centralized place to define which assets should be pulled
 */
object AssetsConfig {

    /**
     * Get the default list of assets to fetch
     * "قائمة الاصول للسحب" (List of assets to pull)
     */
    fun getDefaultAssets(): List<AssetInfo> {
        return listOf(
            AssetInfo(
                url = "https://raw.githubusercontent.com/github/gitignore/main/Android.gitignore",
                fileName = "android_template.txt",
                description = "Android gitignore template"
            ),
            AssetInfo(
                url = "https://raw.githubusercontent.com/github/gitignore/main/Java.gitignore",
                fileName = "java_template.txt",
                description = "Java gitignore template"
            ),
            AssetInfo(
                url = "https://raw.githubusercontent.com/github/gitignore/main/Kotlin.gitignore",
                fileName = "kotlin_template.txt",
                description = "Kotlin gitignore template"
            )
        )
    }

    /**
     * Validate asset URL
     */
    fun isValidAssetUrl(url: String): Boolean {
        return url.startsWith("http://") || url.startsWith("https://")
    }

    /**
     * Validate file name
     */
    fun isValidFileName(fileName: String): Boolean {
        return fileName.isNotEmpty() && !fileName.contains("/") && !fileName.contains("\\")
    }
}
