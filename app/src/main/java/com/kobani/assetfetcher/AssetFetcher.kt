package com.kobani.assetfetcher

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * AssetFetcher - Class responsible for fetching/downloading assets
 * This implements the requirement: "قم ب سحب الاصول الان" (Pull the assets now)
 */
class AssetFetcher(private val context: Context) {

    companion object {
        private const val TAG = "AssetFetcher"
        private const val BUFFER_SIZE = 8192
    }

    /**
     * Fetch an asset from a remote URL and save it locally
     * @param assetUrl The URL of the asset to fetch
     * @param fileName The name to save the asset as
     * @return Result indicating success or failure with message
     */
    suspend fun fetchAsset(assetUrl: String, fileName: String): Result<File> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Starting to fetch asset: $fileName from $assetUrl")
                
                val url = URL(assetUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext Result.failure(
                        Exception("Failed to fetch asset: HTTP ${connection.responseCode}")
                    )
                }

                val assetsDir = File(context.filesDir, "downloaded_assets")
                if (!assetsDir.exists()) {
                    assetsDir.mkdirs()
                }

                val outputFile = File(assetsDir, fileName)
                connection.inputStream.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var bytesRead: Int
                        var totalBytesRead = 0L

                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                        }

                        Log.d(TAG, "Successfully fetched $fileName ($totalBytesRead bytes)")
                    }
                }

                connection.disconnect()
                Result.success(outputFile)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching asset: ${e.message}", e)
                Result.failure(e)
            }
        }
    }

    /**
     * Fetch multiple assets at once
     * @param assets List of asset URLs and file names
     * @return List of results for each asset
     */
    suspend fun fetchAssets(assets: List<AssetInfo>): List<AssetFetchResult> {
        return assets.map { assetInfo ->
            val result = fetchAsset(assetInfo.url, assetInfo.fileName)
            AssetFetchResult(
                assetInfo = assetInfo,
                isSuccess = result.isSuccess,
                file = result.getOrNull(),
                error = result.exceptionOrNull()?.message
            )
        }
    }

    /**
     * Get list of already downloaded assets
     */
    fun getDownloadedAssets(): List<File> {
        val assetsDir = File(context.filesDir, "downloaded_assets")
        return if (assetsDir.exists() && assetsDir.isDirectory) {
            assetsDir.listFiles()?.toList() ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * Clear all downloaded assets
     */
    fun clearDownloadedAssets(): Boolean {
        val assetsDir = File(context.filesDir, "downloaded_assets")
        return if (assetsDir.exists()) {
            assetsDir.deleteRecursively()
        } else {
            true
        }
    }
}

/**
 * Data class representing an asset to be fetched
 */
data class AssetInfo(
    val url: String,
    val fileName: String,
    val description: String = ""
)

/**
 * Data class representing the result of fetching an asset
 */
data class AssetFetchResult(
    val assetInfo: AssetInfo,
    val isSuccess: Boolean,
    val file: File? = null,
    val error: String? = null
)
