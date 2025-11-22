package com.kobani.assetfetcher

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainActivity - Demonstrates asset fetching functionality
 * Implements: "قم ب سحب الاصول الان" (Pull/Fetch the assets now)
 */
class MainActivity : AppCompatActivity() {

    private lateinit var assetFetcher: AssetFetcher
    private lateinit var fetchButton: Button
    private lateinit var clearButton: Button
    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var assetsListText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        assetFetcher = AssetFetcher(this)

        fetchButton = findViewById(R.id.fetch_button)
        clearButton = findViewById(R.id.clear_button)
        statusText = findViewById(R.id.status_text)
        progressBar = findViewById(R.id.progress_bar)
        assetsListText = findViewById(R.id.assets_list_text)

        fetchButton.setOnClickListener {
            fetchAssetsNow()
        }

        clearButton.setOnClickListener {
            clearAssets()
        }

        updateAssetsList()
    }

    /**
     * Fetch assets now - Main functionality
     * "سحب الاصول الان" (Pull the assets now)
     */
    private fun fetchAssetsNow() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                fetchButton.isEnabled = false
                progressBar.visibility = View.VISIBLE
                statusText.text = "جاري سحب الاصول...\nFetching assets..."

                // Sample assets to fetch
                val sampleAssets = listOf(
                    AssetInfo(
                        url = "https://raw.githubusercontent.com/github/gitignore/main/Android.gitignore",
                        fileName = "sample_asset_1.txt",
                        description = "Sample Android gitignore"
                    ),
                    AssetInfo(
                        url = "https://raw.githubusercontent.com/github/gitignore/main/Java.gitignore",
                        fileName = "sample_asset_2.txt",
                        description = "Sample Java gitignore"
                    )
                )

                val results = withContext(Dispatchers.IO) {
                    assetFetcher.fetchAssets(sampleAssets)
                }

                val successCount = results.count { it.isSuccess }
                val failCount = results.count { !it.isSuccess }

                val statusMessage = buildString {
                    append("تم سحب الاصول!\n")
                    append("Assets fetched!\n\n")
                    append("✓ Success: $successCount\n")
                    append("✗ Failed: $failCount\n\n")
                    
                    results.forEach { result ->
                        if (result.isSuccess) {
                            append("✓ ${result.assetInfo.fileName}\n")
                        } else {
                            append("✗ ${result.assetInfo.fileName}: ${result.error}\n")
                        }
                    }
                }

                statusText.text = statusMessage
                updateAssetsList()

            } catch (e: Exception) {
                statusText.text = "خطأ في سحب الاصول\nError: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
                fetchButton.isEnabled = true
            }
        }
    }

    /**
     * Clear all downloaded assets
     */
    private fun clearAssets() {
        val success = assetFetcher.clearDownloadedAssets()
        statusText.text = if (success) {
            "تم مسح الاصول\nAssets cleared"
        } else {
            "فشل في مسح الاصول\nFailed to clear assets"
        }
        updateAssetsList()
    }

    /**
     * Update the list of downloaded assets
     */
    private fun updateAssetsList() {
        val downloadedAssets = assetFetcher.getDownloadedAssets()
        if (downloadedAssets.isEmpty()) {
            assetsListText.text = "لا توجد اصول محملة\nNo assets downloaded"
        } else {
            val assetsList = buildString {
                append("الاصول المحملة:\nDownloaded Assets:\n\n")
                downloadedAssets.forEach { file ->
                    append("• ${file.name} (${file.length() / 1024} KB)\n")
                }
            }
            assetsListText.text = assetsList
        }
    }
}
