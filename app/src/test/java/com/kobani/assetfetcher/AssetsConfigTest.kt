package com.kobani.assetfetcher

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for AssetsConfig
 */
class AssetsConfigTest {

    @Test
    fun testGetDefaultAssets() {
        val assets = AssetsConfig.getDefaultAssets()
        
        assertNotNull("Default assets should not be null", assets)
        assertTrue("Default assets should not be empty", assets.isNotEmpty())
        
        // Verify each asset has required fields
        assets.forEach { asset ->
            assertFalse("Asset URL should not be empty", asset.url.isEmpty())
            assertFalse("Asset fileName should not be empty", asset.fileName.isEmpty())
            assertTrue("Asset URL should be valid", AssetsConfig.isValidAssetUrl(asset.url))
            assertTrue("Asset fileName should be valid", AssetsConfig.isValidFileName(asset.fileName))
        }
    }

    @Test
    fun testIsValidAssetUrl() {
        assertTrue("HTTP URL should be valid", 
            AssetsConfig.isValidAssetUrl("http://example.com/asset.txt"))
        assertTrue("HTTPS URL should be valid", 
            AssetsConfig.isValidAssetUrl("https://example.com/asset.txt"))
        assertFalse("Invalid URL should not be valid", 
            AssetsConfig.isValidAssetUrl("ftp://example.com/asset.txt"))
        assertFalse("Empty URL should not be valid", 
            AssetsConfig.isValidAssetUrl(""))
    }

    @Test
    fun testIsValidFileName() {
        assertTrue("Simple filename should be valid", 
            AssetsConfig.isValidFileName("asset.txt"))
        assertTrue("Filename with underscore should be valid", 
            AssetsConfig.isValidFileName("my_asset.txt"))
        assertFalse("Filename with forward slash should not be valid", 
            AssetsConfig.isValidFileName("folder/asset.txt"))
        assertFalse("Filename with backslash should not be valid", 
            AssetsConfig.isValidFileName("folder\\asset.txt"))
        assertFalse("Empty filename should not be valid", 
            AssetsConfig.isValidFileName(""))
    }

    @Test
    fun testAssetInfoDataClass() {
        val assetInfo = AssetInfo(
            url = "https://example.com/asset.txt",
            fileName = "test_asset.txt",
            description = "Test asset"
        )
        
        assertEquals("URL should match", "https://example.com/asset.txt", assetInfo.url)
        assertEquals("FileName should match", "test_asset.txt", assetInfo.fileName)
        assertEquals("Description should match", "Test asset", assetInfo.description)
    }

    @Test
    fun testAssetInfoWithoutDescription() {
        val assetInfo = AssetInfo(
            url = "https://example.com/asset.txt",
            fileName = "test_asset.txt"
        )
        
        assertEquals("URL should match", "https://example.com/asset.txt", assetInfo.url)
        assertEquals("FileName should match", "test_asset.txt", assetInfo.fileName)
        assertEquals("Description should be empty by default", "", assetInfo.description)
    }
}
