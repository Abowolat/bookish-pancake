package com.kobani.assetfetcher

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.crypto.SecretKey

/**
 * EncryptedAssetFetcher - Fetches assets and stores them encrypted
 * "سحب الاصول المشفرة" (Pull encrypted assets)
 * 
 * Downloads assets from URLs and encrypts them before storing locally
 */
class EncryptedAssetFetcher(private val context: Context) {

    companion object {
        private const val TAG = "EncryptedAssetFetcher"
        private const val BUFFER_SIZE = 8192
        private const val ENCRYPTED_ASSETS_DIR = "encrypted_assets"
        private const val KEY_FILE = "encryption_key.txt"
    }

    private val encryption = AssetEncryption()
    private var encryptionKey: SecretKey? = null

    private val encryptedAssetsDirectory: File
        get() = File(context.filesDir, ENCRYPTED_ASSETS_DIR).also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

    /**
     * Initialize or load encryption key
     */
    fun initializeEncryptionKey(): SecretKey {
        if (encryptionKey != null) {
            return encryptionKey!!
        }

        val keyFile = File(context.filesDir, KEY_FILE)
        
        encryptionKey = if (keyFile.exists()) {
            // Load existing key
            val keyString = keyFile.readText()
            AssetEncryption.stringToKey(keyString)
        } else {
            // Generate new key and save it
            val newKey = AssetEncryption.generateKey()
            keyFile.writeText(AssetEncryption.keyToString(newKey))
            newKey
        }
        
        return encryptionKey!!
    }

    /**
     * Fetch an asset, encrypt it, and store locally
     */
    suspend fun fetchAndEncryptAsset(
        assetUrl: String, 
        fileName: String
    ): Result<File> {
        return withContext(Dispatchers.IO) {
            var connection: HttpURLConnection? = null
            try {
                Log.d(TAG, "Fetching and encrypting asset: $fileName from $assetUrl")
                
                // Download asset
                val url = URL(assetUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    return@withContext Result.failure(
                        Exception("Failed to fetch asset: HTTP ${connection.responseCode}")
                    )
                }

                // Read data
                val data = connection.inputStream.readBytes()
                
                // Encrypt data
                val key = initializeEncryptionKey()
                val encryptedData = encryption.encrypt(data, key)
                
                // Save encrypted data
                val outputFile = File(encryptedAssetsDirectory, "$fileName.enc")
                FileOutputStream(outputFile).use { output ->
                    output.write(encryptedData)
                }

                Log.d(TAG, "Successfully encrypted and saved $fileName (${encryptedData.size} bytes)")
                Result.success(outputFile)
                
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching/encrypting asset: ${e.message}", e)
                Result.failure(e)
            } finally {
                connection?.disconnect()
            }
        }
    }

    /**
     * Fetch and encrypt multiple assets
     */
    suspend fun fetchAndEncryptAssets(
        assets: List<EncryptedAssetInfo>
    ): List<EncryptedAssetResult> {
        return assets.map { assetInfo ->
            val result = fetchAndEncryptAsset(assetInfo.url, assetInfo.fileName)
            EncryptedAssetResult(
                assetInfo = assetInfo,
                isSuccess = result.isSuccess,
                encryptedFile = result.getOrNull(),
                error = result.exceptionOrNull()?.message
            )
        }
    }

    /**
     * Decrypt and read an encrypted asset
     */
    fun decryptAsset(fileName: String): Result<ByteArray> {
        return try {
            val encryptedFile = File(encryptedAssetsDirectory, "$fileName.enc")
            if (!encryptedFile.exists()) {
                return Result.failure(Exception("Encrypted asset not found: $fileName"))
            }

            val encryptedData = FileInputStream(encryptedFile).use { it.readBytes() }
            val key = initializeEncryptionKey()
            val decryptedData = encryption.decrypt(encryptedData, key)
            
            Result.success(decryptedData)
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting asset: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get list of encrypted assets
     */
    fun getEncryptedAssets(): List<File> {
        return encryptedAssetsDirectory.listFiles()
            ?.filter { it.extension == "enc" }
            ?: emptyList()
    }

    /**
     * Delete a specific encrypted asset
     */
    fun deleteEncryptedAsset(fileName: String): Boolean {
        val file = File(encryptedAssetsDirectory, "$fileName.enc")
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    /**
     * Delete all encrypted assets
     */
    fun deleteAllEncryptedAssets(): Boolean {
        return try {
            encryptedAssetsDirectory.deleteRecursively()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting encrypted assets: ${e.message}", e)
            false
        }
    }

    /**
     * Get total size of encrypted assets
     */
    fun getEncryptedAssetsSize(): Long {
        return getEncryptedAssets().sumOf { it.length() }
    }
}
