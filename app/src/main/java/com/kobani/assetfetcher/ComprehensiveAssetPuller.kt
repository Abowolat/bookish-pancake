package com.kobani.assetfetcher

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/**
 * ComprehensiveAssetPuller - Pulls all types of assets
 * "سحب جميع الاصول" (Pull all assets)
 * 
 * Handles regular assets, encrypted assets, and card assets
 * This implements the complete requirement: "سحب جميع الاصول مشفر بطاقات كل شي"
 * (Pull all encrypted assets, cards, everything)
 */
class ComprehensiveAssetPuller(private val context: Context) {

    companion object {
        private const val TAG = "ComprehensiveAssetPuller"
    }

    private val assetFetcher = AssetFetcher(context)
    private val encryptedAssetFetcher = EncryptedAssetFetcher(context)
    private val cardAssetFetcher = CardAssetFetcher(context)

    /**
     * Pull all assets: regular, encrypted, and cards
     * "سحب الان" (Pull now)
     */
    suspend fun pullAllAssetsNow(): ComprehensivePullResult {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "Starting comprehensive asset pull - all types")

            try {
                // Get asset configurations
                val regularAssets = AssetsConfig.getDefaultAssets()
                val encryptedAssets = getEncryptedAssetsConfig()
                val cardAssets = getCardAssetsConfig()

                // Pull all asset types in parallel
                val regularResultsDeferred = async {
                    assetFetcher.fetchAssets(regularAssets)
                }

                val encryptedResultsDeferred = async {
                    encryptedAssetFetcher.fetchAndEncryptAssets(encryptedAssets)
                }

                val cardResultsDeferred = async {
                    cardAssetFetcher.fetchCards(cardAssets)
                }

                // Wait for all results
                val regularResults = regularResultsDeferred.await()
                val encryptedResults = encryptedResultsDeferred.await()
                val cardResults = cardResultsDeferred.await()

                ComprehensivePullResult(
                    regularAssets = regularResults,
                    encryptedAssets = encryptedResults,
                    cardAssets = cardResults,
                    isSuccess = true,
                    error = null
                )

            } catch (e: Exception) {
                Log.e(TAG, "Error during comprehensive pull: ${e.message}", e)
                ComprehensivePullResult(
                    regularAssets = emptyList(),
                    encryptedAssets = emptyList(),
                    cardAssets = emptyList(),
                    isSuccess = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Pull only encrypted assets
     */
    suspend fun pullEncryptedAssetsOnly(): List<EncryptedAssetResult> {
        return withContext(Dispatchers.IO) {
            val encryptedAssets = getEncryptedAssetsConfig()
            encryptedAssetFetcher.fetchAndEncryptAssets(encryptedAssets)
        }
    }

    /**
     * Pull only card assets
     */
    suspend fun pullCardAssetsOnly(): List<CardFetchResult> {
        return withContext(Dispatchers.IO) {
            val cardAssets = getCardAssetsConfig()
            cardAssetFetcher.fetchCards(cardAssets)
        }
    }

    /**
     * Get summary of all downloaded assets
     */
    fun getAllAssetsSummary(): AssetsSummary {
        val regularAssets = assetFetcher.getDownloadedAssets()
        val encryptedAssets = encryptedAssetFetcher.getEncryptedAssets()
        val cardAssets = cardAssetFetcher.getAllCards()

        return AssetsSummary(
            regularAssetsCount = regularAssets.size,
            regularAssetsSize = regularAssets.sumOf { it.length() },
            encryptedAssetsCount = encryptedAssets.size,
            encryptedAssetsSize = encryptedAssets.sumOf { it.length() },
            cardsCount = cardAssets.size,
            totalCount = regularAssets.size + encryptedAssets.size + cardAssets.size
        )
    }

    /**
     * Clear all assets (regular, encrypted, and cards)
     */
    fun clearAllAssets(): Boolean {
        val regularCleared = assetFetcher.clearDownloadedAssets()
        val encryptedCleared = encryptedAssetFetcher.deleteAllEncryptedAssets()
        val cardsCleared = cardAssetFetcher.deleteAllCards()
        
        return regularCleared && encryptedCleared && cardsCleared
    }

    /**
     * Get configuration for encrypted assets
     */
    private fun getEncryptedAssetsConfig(): List<EncryptedAssetInfo> {
        return listOf(
            EncryptedAssetInfo(
                url = "https://raw.githubusercontent.com/github/gitignore/main/Python.gitignore",
                fileName = "encrypted_python_template",
                description = "Encrypted Python gitignore template",
                isEncrypted = true
            ),
            EncryptedAssetInfo(
                url = "https://raw.githubusercontent.com/github/gitignore/main/Node.gitignore",
                fileName = "encrypted_node_template",
                description = "Encrypted Node.js gitignore template",
                isEncrypted = true
            ),
            EncryptedAssetInfo(
                url = "https://raw.githubusercontent.com/github/gitignore/main/Go.gitignore",
                fileName = "encrypted_go_template",
                description = "Encrypted Go gitignore template",
                isEncrypted = true
            )
        )
    }

    /**
     * Get configuration for card assets
     */
    private fun getCardAssetsConfig(): List<CardAsset> {
        return listOf(
            CardAsset(
                id = "card_001",
                title = "Android Development Template",
                content = "Template for Android development projects",
                category = "Development",
                url = "https://raw.githubusercontent.com/github/gitignore/main/Android.gitignore",
                metadata = mapOf(
                    "language" to "Kotlin",
                    "platform" to "Android",
                    "version" to "1.0"
                )
            ),
            CardAsset(
                id = "card_002",
                title = "Java Configuration",
                content = "Java project configuration template",
                category = "Development",
                url = "https://raw.githubusercontent.com/github/gitignore/main/Java.gitignore",
                metadata = mapOf(
                    "language" to "Java",
                    "type" to "Configuration",
                    "version" to "1.0"
                )
            ),
            CardAsset(
                id = "card_003",
                title = "Gradle Build Configuration",
                content = "Gradle build system configuration",
                category = "Build",
                url = "https://raw.githubusercontent.com/github/gitignore/main/Gradle.gitignore",
                metadata = mapOf(
                    "tool" to "Gradle",
                    "type" to "Build Configuration",
                    "version" to "1.0"
                )
            )
        )
    }
}

/**
 * Result of comprehensive asset pull operation
 */
data class ComprehensivePullResult(
    val regularAssets: List<AssetFetchResult>,
    val encryptedAssets: List<EncryptedAssetResult>,
    val cardAssets: List<CardFetchResult>,
    val isSuccess: Boolean,
    val error: String? = null
) {
    fun getSuccessCount(): Int {
        return regularAssets.count { it.isSuccess } +
               encryptedAssets.count { it.isSuccess } +
               cardAssets.count { it.isSuccess }
    }

    fun getTotalCount(): Int {
        return regularAssets.size + encryptedAssets.size + cardAssets.size
    }

    fun getFailureCount(): Int {
        return getTotalCount() - getSuccessCount()
    }
}

/**
 * Summary of all assets
 */
data class AssetsSummary(
    val regularAssetsCount: Int,
    val regularAssetsSize: Long,
    val encryptedAssetsCount: Int,
    val encryptedAssetsSize: Long,
    val cardsCount: Int,
    val totalCount: Int
)
