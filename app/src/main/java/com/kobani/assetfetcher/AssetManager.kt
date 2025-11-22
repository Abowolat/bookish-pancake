package com.kobani.assetfetcher

import android.content.Context
import android.util.Log
import java.io.File

/**
 * AssetManager - Utility class for managing downloaded assets
 * "مدير الاصول" (Assets Manager)
 */
class AssetManager(private val context: Context) {

    companion object {
        private const val TAG = "AssetManager"
        private const val ASSETS_DIR = "downloaded_assets"
    }

    private val assetsDirectory: File
        get() = File(context.filesDir, ASSETS_DIR).also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

    /**
     * Check if an asset exists locally
     */
    fun assetExists(fileName: String): Boolean {
        return File(assetsDirectory, fileName).exists()
    }

    /**
     * Get the file for a specific asset
     */
    fun getAssetFile(fileName: String): File? {
        val file = File(assetsDirectory, fileName)
        return if (file.exists()) file else null
    }

    /**
     * Get all downloaded assets
     */
    fun getAllAssets(): List<File> {
        return assetsDirectory.listFiles()?.toList() ?: emptyList()
    }

    /**
     * Get total size of all downloaded assets in bytes
     */
    fun getTotalAssetsSize(): Long {
        return getAllAssets().sumOf { it.length() }
    }

    /**
     * Delete a specific asset
     */
    fun deleteAsset(fileName: String): Boolean {
        val file = File(assetsDirectory, fileName)
        return if (file.exists()) {
            val deleted = file.delete()
            Log.d(TAG, "Deleted asset $fileName: $deleted")
            deleted
        } else {
            Log.w(TAG, "Asset $fileName does not exist")
            false
        }
    }

    /**
     * Delete all assets
     */
    fun deleteAllAssets(): Boolean {
        return try {
            assetsDirectory.deleteRecursively()
            Log.d(TAG, "All assets deleted")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting assets: ${e.message}", e)
            false
        }
    }

    /**
     * Get asset information
     */
    fun getAssetInfo(fileName: String): AssetFileInfo? {
        val file = File(assetsDirectory, fileName)
        return if (file.exists()) {
            AssetFileInfo(
                fileName = file.name,
                size = file.length(),
                lastModified = file.lastModified(),
                path = file.absolutePath
            )
        } else {
            null
        }
    }
}

/**
 * Data class containing information about a downloaded asset file
 */
data class AssetFileInfo(
    val fileName: String,
    val size: Long,
    val lastModified: Long,
    val path: String
)
