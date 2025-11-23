package com.kobani.assetfetcher

import org.junit.Assert.*
import org.junit.Test
import org.json.JSONObject

/**
 * Unit tests for CardAsset
 */
class CardAssetTest {

    @Test
    fun testCardAssetCreation() {
        val card = CardAsset(
            id = "test_001",
            title = "Test Card",
            content = "Test content",
            category = "Testing",
            url = "https://example.com",
            metadata = mapOf("key1" to "value1", "key2" to "value2"),
            isEncrypted = false
        )
        
        assertEquals("test_001", card.id)
        assertEquals("Test Card", card.title)
        assertEquals("Test content", card.content)
        assertEquals("Testing", card.category)
        assertEquals("https://example.com", card.url)
        assertEquals(2, card.metadata.size)
        assertFalse(card.isEncrypted)
    }

    @Test
    fun testCardAssetToJson() {
        val card = CardAsset(
            id = "test_001",
            title = "Test Card",
            content = "Test content",
            category = "Testing",
            url = "https://example.com",
            metadata = mapOf("key1" to "value1"),
            isEncrypted = false
        )
        
        val json = card.toJson()
        assertNotNull(json)
        assertTrue(json.contains("test_001"))
        assertTrue(json.contains("Test Card"))
        assertTrue(json.contains("Test content"))
    }

    @Test
    fun testCardAssetFromJson() {
        val jsonString = """
            {
                "id": "test_001",
                "title": "Test Card",
                "content": "Test content",
                "category": "Testing",
                "url": "https://example.com",
                "isEncrypted": false,
                "metadata": {
                    "key1": "value1",
                    "key2": "value2"
                }
            }
        """.trimIndent()
        
        val card = CardAsset.fromJson(jsonString)
        
        assertEquals("test_001", card.id)
        assertEquals("Test Card", card.title)
        assertEquals("Test content", card.content)
        assertEquals("Testing", card.category)
        assertEquals("https://example.com", card.url)
        assertFalse(card.isEncrypted)
        assertEquals(2, card.metadata.size)
        assertEquals("value1", card.metadata["key1"])
        assertEquals("value2", card.metadata["key2"])
    }

    @Test
    fun testCardAssetJsonRoundTrip() {
        val originalCard = CardAsset(
            id = "test_001",
            title = "Test Card",
            content = "Test content",
            category = "Testing",
            url = "https://example.com",
            metadata = mapOf("author" to "tester", "version" to "1.0"),
            isEncrypted = true
        )
        
        val json = originalCard.toJson()
        val restoredCard = CardAsset.fromJson(json)
        
        assertEquals(originalCard.id, restoredCard.id)
        assertEquals(originalCard.title, restoredCard.title)
        assertEquals(originalCard.content, restoredCard.content)
        assertEquals(originalCard.category, restoredCard.category)
        assertEquals(originalCard.url, restoredCard.url)
        assertEquals(originalCard.isEncrypted, restoredCard.isEncrypted)
        assertEquals(originalCard.metadata.size, restoredCard.metadata.size)
    }

    @Test
    fun testCardAssetWithEmptyMetadata() {
        val card = CardAsset(
            id = "test_001",
            title = "Test Card",
            content = "Test content"
        )
        
        assertEquals("test_001", card.id)
        assertEquals("Test Card", card.title)
        assertEquals("", card.category)
        assertEquals("", card.url)
        assertTrue(card.metadata.isEmpty())
        assertFalse(card.isEncrypted)
    }

    @Test
    fun testCardAssetWithArabicContent() {
        val card = CardAsset(
            id = "arabic_001",
            title = "بطاقة اختبار",
            content = "محتوى اختبار عربي",
            category = "اختبار",
            url = "https://example.com",
            metadata = mapOf("اللغة" to "العربية")
        )
        
        val json = card.toJson()
        val restored = CardAsset.fromJson(json)
        
        assertEquals("بطاقة اختبار", restored.title)
        assertEquals("محتوى اختبار عربي", restored.content)
        assertEquals("اختبار", restored.category)
        assertEquals("العربية", restored.metadata["اللغة"])
    }

    @Test
    fun testCardFetchResult() {
        val card = CardAsset(
            id = "test_001",
            title = "Test Card",
            content = "Test content"
        )
        
        val successResult = CardFetchResult(
            card = card,
            isSuccess = true,
            error = null
        )
        
        assertTrue(successResult.isSuccess)
        assertNull(successResult.error)
        assertEquals(card, successResult.card)
        
        val failureResult = CardFetchResult(
            card = card,
            isSuccess = false,
            error = "Network error"
        )
        
        assertFalse(failureResult.isSuccess)
        assertEquals("Network error", failureResult.error)
    }

    @Test
    fun testCardAssetWithSpecialCharacters() {
        val card = CardAsset(
            id = "special_001",
            title = "Test 🎴 Card",
            content = "Content with <>&\"' special chars",
            metadata = mapOf("emoji" to "😀", "symbols" to "<>&")
        )
        
        val json = card.toJson()
        val restored = CardAsset.fromJson(json)
        
        assertEquals(card.title, restored.title)
        assertEquals(card.content, restored.content)
        assertEquals(card.metadata["emoji"], restored.metadata["emoji"])
    }

    @Test
    fun testCardAssetMinimalJson() {
        val minimalJson = """
            {
                "id": "min_001",
                "title": "Minimal",
                "content": "Minimal content"
            }
        """.trimIndent()
        
        val card = CardAsset.fromJson(minimalJson)
        
        assertEquals("min_001", card.id)
        assertEquals("Minimal", card.title)
        assertEquals("Minimal content", card.content)
        assertEquals("", card.category)
        assertEquals("", card.url)
        assertFalse(card.isEncrypted)
        assertTrue(card.metadata.isEmpty())
    }
}
