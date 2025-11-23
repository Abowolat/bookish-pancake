package com.kobani.assetfetcher

import org.junit.Assert.*
import org.junit.Test
import javax.crypto.SecretKey

/**
 * Unit tests for AssetEncryption
 */
class AssetEncryptionTest {

    private val encryption = AssetEncryption()

    @Test
    fun testGenerateKey() {
        val key = AssetEncryption.generateKey()
        assertNotNull("Generated key should not be null", key)
        assertEquals("Key algorithm should be AES", "AES", key.algorithm)
    }

    @Test
    fun testKeyToStringAndBack() {
        val originalKey = AssetEncryption.generateKey()
        val keyString = AssetEncryption.keyToString(originalKey)
        
        assertNotNull("Key string should not be null", keyString)
        assertFalse("Key string should not be empty", keyString.isEmpty())
        
        val restoredKey = AssetEncryption.stringToKey(keyString)
        assertArrayEquals(
            "Restored key should match original",
            originalKey.encoded,
            restoredKey.encoded
        )
    }

    @Test
    fun testEncryptDecryptData() {
        val key = AssetEncryption.generateKey()
        val originalData = "Test data for encryption".toByteArray(Charsets.UTF_8)
        
        val encrypted = encryption.encrypt(originalData, key)
        assertNotNull("Encrypted data should not be null", encrypted)
        assertTrue("Encrypted data should be larger (includes IV)", encrypted.size > originalData.size)
        assertFalse(
            "Encrypted data should not match original",
            encrypted.contentEquals(originalData)
        )
        
        val decrypted = encryption.decrypt(encrypted, key)
        assertArrayEquals(
            "Decrypted data should match original",
            originalData,
            decrypted
        )
    }

    @Test
    fun testEncryptDecryptString() {
        val key = AssetEncryption.generateKey()
        val originalText = "مرحبا - Hello World! 123"
        
        val encrypted = encryption.encryptString(originalText, key)
        assertNotNull("Encrypted string should not be null", encrypted)
        assertNotEquals("Encrypted should not match original", originalText, encrypted)
        
        val decrypted = encryption.decryptString(encrypted, key)
        assertEquals("Decrypted text should match original", originalText, decrypted)
    }

    @Test
    fun testEncryptionWithDifferentKeys() {
        val key1 = AssetEncryption.generateKey()
        val key2 = AssetEncryption.generateKey()
        val data = "Test data".toByteArray(Charsets.UTF_8)
        
        val encrypted1 = encryption.encrypt(data, key1)
        val encrypted2 = encryption.encrypt(data, key2)
        
        assertFalse(
            "Same data encrypted with different keys should produce different results",
            encrypted1.contentEquals(encrypted2)
        )
    }

    @Test
    fun testEncryptedAssetInfo() {
        val assetInfo = EncryptedAssetInfo(
            url = "https://example.com/asset.txt",
            fileName = "test_encrypted",
            description = "Test encrypted asset",
            isEncrypted = true,
            encryptionKey = "test_key"
        )
        
        assertEquals("https://example.com/asset.txt", assetInfo.url)
        assertEquals("test_encrypted", assetInfo.fileName)
        assertTrue(assetInfo.isEncrypted)
        assertEquals("test_key", assetInfo.encryptionKey)
    }

    @Test
    fun testEncryptedAssetResult() {
        val assetInfo = EncryptedAssetInfo(
            url = "https://example.com/asset.txt",
            fileName = "test",
            isEncrypted = true
        )
        
        val result = EncryptedAssetResult(
            assetInfo = assetInfo,
            isSuccess = true,
            encryptedFile = null,
            error = null
        )
        
        assertTrue(result.isSuccess)
        assertNull(result.error)
    }

    @Test
    fun testEncryptLargeData() {
        val key = AssetEncryption.generateKey()
        val largeData = ByteArray(10000) { it.toByte() }
        
        val encrypted = encryption.encrypt(largeData, key)
        val decrypted = encryption.decrypt(encrypted, key)
        
        assertArrayEquals("Large data should encrypt/decrypt correctly", largeData, decrypted)
    }

    @Test
    fun testEncryptEmptyData() {
        val key = AssetEncryption.generateKey()
        val emptyData = ByteArray(0)
        
        val encrypted = encryption.encrypt(emptyData, key)
        val decrypted = encryption.decrypt(encrypted, key)
        
        assertArrayEquals("Empty data should encrypt/decrypt correctly", emptyData, decrypted)
    }

    @Test
    fun testEncryptUnicodeString() {
        val key = AssetEncryption.generateKey()
        val unicodeText = "مرحبا بكم في اختبار التشفير 🔒 Hello 你好"
        
        val encrypted = encryption.encryptString(unicodeText, key)
        val decrypted = encryption.decryptString(encrypted, key)
        
        assertEquals("Unicode text should encrypt/decrypt correctly", unicodeText, decrypted)
    }
}
