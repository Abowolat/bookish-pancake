package com.kobani.assetfetcher

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ComprehensivePullResult and AssetsSummary
 */
class ComprehensivePullTest {

    @Test
    fun testComprehensivePullResultSuccessCounting() {
        val regularResults = listOf(
            AssetFetchResult(
                assetInfo = AssetInfo("url1", "file1.txt"),
                isSuccess = true,
                file = null
            ),
            AssetFetchResult(
                assetInfo = AssetInfo("url2", "file2.txt"),
                isSuccess = true,
                file = null
            )
        )
        
        val encryptedResults = listOf(
            EncryptedAssetResult(
                assetInfo = EncryptedAssetInfo("url3", "file3.txt", isEncrypted = true),
                isSuccess = true
            ),
            EncryptedAssetResult(
                assetInfo = EncryptedAssetInfo("url4", "file4.txt", isEncrypted = true),
                isSuccess = false,
                error = "Network error"
            )
        )
        
        val cardResults = listOf(
            CardFetchResult(
                card = CardAsset("card1", "Card 1", "Content 1"),
                isSuccess = true
            )
        )
        
        val result = ComprehensivePullResult(
            regularAssets = regularResults,
            encryptedAssets = encryptedResults,
            cardAssets = cardResults,
            isSuccess = true
        )
        
        assertEquals(4, result.getSuccessCount())
        assertEquals(5, result.getTotalCount())
        assertEquals(1, result.getFailureCount())
    }

    @Test
    fun testComprehensivePullResultAllSuccess() {
        val result = ComprehensivePullResult(
            regularAssets = listOf(
                AssetFetchResult(
                    assetInfo = AssetInfo("url1", "file1.txt"),
                    isSuccess = true,
                    file = null
                )
            ),
            encryptedAssets = listOf(
                EncryptedAssetResult(
                    assetInfo = EncryptedAssetInfo("url2", "file2.txt", isEncrypted = true),
                    isSuccess = true
                )
            ),
            cardAssets = listOf(
                CardFetchResult(
                    card = CardAsset("card1", "Card 1", "Content"),
                    isSuccess = true
                )
            ),
            isSuccess = true
        )
        
        assertEquals(3, result.getSuccessCount())
        assertEquals(3, result.getTotalCount())
        assertEquals(0, result.getFailureCount())
        assertTrue(result.isSuccess)
        assertNull(result.error)
    }

    @Test
    fun testComprehensivePullResultAllFailure() {
        val result = ComprehensivePullResult(
            regularAssets = listOf(
                AssetFetchResult(
                    assetInfo = AssetInfo("url1", "file1.txt"),
                    isSuccess = false,
                    error = "Failed"
                )
            ),
            encryptedAssets = listOf(
                EncryptedAssetResult(
                    assetInfo = EncryptedAssetInfo("url2", "file2.txt", isEncrypted = true),
                    isSuccess = false,
                    error = "Failed"
                )
            ),
            cardAssets = listOf(
                CardFetchResult(
                    card = CardAsset("card1", "Card 1", "Content"),
                    isSuccess = false,
                    error = "Failed"
                )
            ),
            isSuccess = false,
            error = "Overall failure"
        )
        
        assertEquals(0, result.getSuccessCount())
        assertEquals(3, result.getTotalCount())
        assertEquals(3, result.getFailureCount())
        assertFalse(result.isSuccess)
        assertEquals("Overall failure", result.error)
    }

    @Test
    fun testComprehensivePullResultEmptyLists() {
        val result = ComprehensivePullResult(
            regularAssets = emptyList(),
            encryptedAssets = emptyList(),
            cardAssets = emptyList(),
            isSuccess = true
        )
        
        assertEquals(0, result.getSuccessCount())
        assertEquals(0, result.getTotalCount())
        assertEquals(0, result.getFailureCount())
    }

    @Test
    fun testAssetsSummary() {
        val summary = AssetsSummary(
            regularAssetsCount = 5,
            regularAssetsSize = 1024 * 100, // 100 KB
            encryptedAssetsCount = 3,
            encryptedAssetsSize = 1024 * 50, // 50 KB
            cardsCount = 10,
            totalCount = 18
        )
        
        assertEquals(5, summary.regularAssetsCount)
        assertEquals(102400, summary.regularAssetsSize)
        assertEquals(3, summary.encryptedAssetsCount)
        assertEquals(51200, summary.encryptedAssetsSize)
        assertEquals(10, summary.cardsCount)
        assertEquals(18, summary.totalCount)
    }

    @Test
    fun testAssetsSummaryZeroCounts() {
        val summary = AssetsSummary(
            regularAssetsCount = 0,
            regularAssetsSize = 0,
            encryptedAssetsCount = 0,
            encryptedAssetsSize = 0,
            cardsCount = 0,
            totalCount = 0
        )
        
        assertEquals(0, summary.regularAssetsCount)
        assertEquals(0, summary.regularAssetsSize)
        assertEquals(0, summary.encryptedAssetsCount)
        assertEquals(0, summary.encryptedAssetsSize)
        assertEquals(0, summary.cardsCount)
        assertEquals(0, summary.totalCount)
    }

    @Test
    fun testAssetsSummaryLargeSizes() {
        val summary = AssetsSummary(
            regularAssetsCount = 100,
            regularAssetsSize = 1024L * 1024 * 100, // 100 MB
            encryptedAssetsCount = 50,
            encryptedAssetsSize = 1024L * 1024 * 50, // 50 MB
            cardsCount = 200,
            totalCount = 350
        )
        
        assertEquals(100, summary.regularAssetsCount)
        assertEquals(104857600L, summary.regularAssetsSize)
        assertEquals(50, summary.encryptedAssetsCount)
        assertEquals(52428800L, summary.encryptedAssetsSize)
        assertEquals(200, summary.cardsCount)
        assertEquals(350, summary.totalCount)
    }

    @Test
    fun testEncryptedAssetInfoDefaults() {
        val assetInfo = EncryptedAssetInfo(
            url = "https://example.com/asset.txt",
            fileName = "test.txt"
        )
        
        assertEquals("https://example.com/asset.txt", assetInfo.url)
        assertEquals("test.txt", assetInfo.fileName)
        assertEquals("", assetInfo.description)
        assertTrue(assetInfo.isEncrypted)
        assertNull(assetInfo.encryptionKey)
    }

    @Test
    fun testEncryptedAssetInfoWithAllFields() {
        val assetInfo = EncryptedAssetInfo(
            url = "https://example.com/asset.txt",
            fileName = "test.txt",
            description = "Test asset",
            isEncrypted = true,
            encryptionKey = "key123"
        )
        
        assertEquals("https://example.com/asset.txt", assetInfo.url)
        assertEquals("test.txt", assetInfo.fileName)
        assertEquals("Test asset", assetInfo.description)
        assertTrue(assetInfo.isEncrypted)
        assertEquals("key123", assetInfo.encryptionKey)
    }

    @Test
    fun testMixedResultCombination() {
        // Simulate real-world scenario with mixed success/failure
        val result = ComprehensivePullResult(
            regularAssets = listOf(
                AssetFetchResult(AssetInfo("url1", "f1.txt"), true, null),
                AssetFetchResult(AssetInfo("url2", "f2.txt"), true, null),
                AssetFetchResult(AssetInfo("url3", "f3.txt"), false, null, "Error")
            ),
            encryptedAssets = listOf(
                EncryptedAssetResult(EncryptedAssetInfo("url4", "f4.txt", isEncrypted = true), true),
                EncryptedAssetResult(EncryptedAssetInfo("url5", "f5.txt", isEncrypted = true), true)
            ),
            cardAssets = listOf(
                CardFetchResult(CardAsset("c1", "Card 1", "Content"), true),
                CardFetchResult(CardAsset("c2", "Card 2", "Content"), false, "Error"),
                CardFetchResult(CardAsset("c3", "Card 3", "Content"), true)
            ),
            isSuccess = true
        )
        
        assertEquals(6, result.getSuccessCount()) // 2 + 2 + 2 successful
        assertEquals(8, result.getTotalCount())
        assertEquals(2, result.getFailureCount()) // 1 + 0 + 1 failed
    }
}
