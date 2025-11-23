package com.kobani.assetfetcher

import android.util.Base64
import android.util.Log
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AssetEncryption - Handles encryption and decryption of assets
 * "تشفير الاصول" (Asset Encryption)
 * 
 * Implements AES-256 encryption for secure asset storage
 */
class AssetEncryption {

    companion object {
        private const val TAG = "AssetEncryption"
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
        private const val KEY_SIZE = 256
        private const val IV_SIZE = 16

        /**
         * Generate a new encryption key
         */
        fun generateKey(): SecretKey {
            val keyGenerator = KeyGenerator.getInstance(ALGORITHM)
            keyGenerator.init(KEY_SIZE)
            return keyGenerator.generateKey()
        }

        /**
         * Generate a random initialization vector
         */
        private fun generateIV(): ByteArray {
            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)
            return iv
        }

        /**
         * Convert key to Base64 string for storage
         */
        fun keyToString(key: SecretKey): String {
            return Base64.encodeToString(key.encoded, Base64.NO_WRAP)
        }

        /**
         * Convert Base64 string back to key
         */
        fun stringToKey(keyString: String): SecretKey {
            val decodedKey = Base64.decode(keyString, Base64.NO_WRAP)
            return SecretKeySpec(decodedKey, 0, decodedKey.size, ALGORITHM)
        }
    }

    /**
     * Encrypt data using AES encryption
     * @param data Data to encrypt
     * @param key Encryption key
     * @return Encrypted data with IV prepended (IV + encrypted data)
     */
    fun encrypt(data: ByteArray, key: SecretKey): ByteArray {
        return try {
            val iv = generateIV()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
            val encryptedData = cipher.doFinal(data)
            
            // Prepend IV to encrypted data
            iv + encryptedData
        } catch (e: Exception) {
            Log.e(TAG, "Error encrypting data: ${e.message}", e)
            throw e
        }
    }

    /**
     * Decrypt data using AES decryption
     * @param encryptedData Encrypted data with IV prepended
     * @param key Decryption key
     * @return Decrypted data
     */
    fun decrypt(encryptedData: ByteArray, key: SecretKey): ByteArray {
        return try {
            // Extract IV from the beginning
            val iv = encryptedData.copyOfRange(0, IV_SIZE)
            val actualEncryptedData = encryptedData.copyOfRange(IV_SIZE, encryptedData.size)
            
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            cipher.doFinal(actualEncryptedData)
        } catch (e: Exception) {
            Log.e(TAG, "Error decrypting data: ${e.message}", e)
            throw e
        }
    }

    /**
     * Encrypt a string
     */
    fun encryptString(text: String, key: SecretKey): String {
        val encrypted = encrypt(text.toByteArray(Charsets.UTF_8), key)
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    /**
     * Decrypt a string
     */
    fun decryptString(encryptedText: String, key: SecretKey): String {
        val encryptedData = Base64.decode(encryptedText, Base64.NO_WRAP)
        val decrypted = decrypt(encryptedData, key)
        return String(decrypted, Charsets.UTF_8)
    }
}

/**
 * Data class representing an encrypted asset
 */
data class EncryptedAssetInfo(
    val url: String,
    val fileName: String,
    val description: String = "",
    val isEncrypted: Boolean = true,
    val encryptionKey: String? = null
)

/**
 * Result of encrypted asset operation
 */
data class EncryptedAssetResult(
    val assetInfo: EncryptedAssetInfo,
    val isSuccess: Boolean,
    val encryptedFile: java.io.File? = null,
    val error: String? = null
)
