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
 * MainActivity - Demonstrates comprehensive asset fetching functionality
 * Implements: "قم ب سحب الاصول الان" (Pull/Fetch the assets now)
 * Enhanced to support: "سحب جميع الاصول مشفر بطاقات كل شي" 
 * (Pull all encrypted assets, cards, everything)
 */
class MainActivity : AppCompatActivity() {

    private lateinit var assetFetcher: AssetFetcher
    private lateinit var comprehensiveAssetPuller: ComprehensiveAssetPuller
    private lateinit var fetchButton: Button
    private lateinit var clearButton: Button
    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var assetsListText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        assetFetcher = AssetFetcher(this)
        comprehensiveAssetPuller = ComprehensiveAssetPuller(this)

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
     * Fetch all assets now - Main functionality
     * "سحب جميع الاصول مشفر بطاقات كل شي الان" 
     * (Pull all encrypted assets, cards, everything now)
     */
    private fun fetchAssetsNow() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                fetchButton.isEnabled = false
                progressBar.visibility = View.VISIBLE
                statusText.text = "جاري سحب جميع الاصول المشفرة والبطاقات...\nPulling all encrypted assets and cards..."

                // Pull ALL assets: regular, encrypted, and cards
                val result = withContext(Dispatchers.IO) {
                    comprehensiveAssetPuller.pullAllAssetsNow()
                }

                val statusMessage = buildString {
                    append("✅ تم سحب جميع الاصول!\n")
                    append("✅ All assets pulled successfully!\n\n")
                    append("📊 Summary:\n")
                    append("━━━━━━━━━━━━━━━━━━\n")
                    append("✓ Success: ${result.getSuccessCount()}\n")
                    append("✗ Failed: ${result.getFailureCount()}\n")
                    append("📦 Total: ${result.getTotalCount()}\n\n")
                    
                    // Regular assets
                    append("📁 Regular Assets: ${result.regularAssets.size}\n")
                    result.regularAssets.forEach { assetResult ->
                        val icon = if (assetResult.isSuccess) "✓" else "✗"
                        append("  $icon ${assetResult.assetInfo.fileName}\n")
                    }
                    
                    // Encrypted assets
                    append("\n🔒 Encrypted Assets: ${result.encryptedAssets.size}\n")
                    result.encryptedAssets.forEach { encResult ->
                        val icon = if (encResult.isSuccess) "✓" else "✗"
                        append("  $icon ${encResult.assetInfo.fileName} [ENCRYPTED]\n")
                    }
                    
                    // Card assets
                    append("\n🎴 Card Assets: ${result.cardAssets.size}\n")
                    result.cardAssets.forEach { cardResult ->
                        val icon = if (cardResult.isSuccess) "✓" else "✗"
                        append("  $icon ${cardResult.card.title}\n")
                    }
                }

                statusText.text = statusMessage
                updateAssetsList()

            } catch (e: Exception) {
                statusText.text = "❌ خطأ في سحب الاصول\n❌ Error: ${e.message}"
            } finally {
                progressBar.visibility = View.GONE
                fetchButton.isEnabled = true
            }
        }
    }

    /**
     * Clear all downloaded assets (regular, encrypted, and cards)
     */
    private fun clearAssets() {
        val success = comprehensiveAssetPuller.clearAllAssets()
        statusText.text = if (success) {
            "✅ تم مسح جميع الاصول (عادية، مشفرة، بطاقات)\n✅ All assets cleared (regular, encrypted, cards)"
        } else {
            "❌ فشل في مسح الاصول\n❌ Failed to clear assets"
        }
        updateAssetsList()
    }

    /**
     * Update the list of all downloaded assets (regular, encrypted, cards)
     */
    private fun updateAssetsList() {
        val summary = comprehensiveAssetPuller.getAllAssetsSummary()
        
        if (summary.totalCount == 0) {
            assetsListText.text = "لا توجد اصول محملة\nNo assets downloaded"
        } else {
            val assetsList = buildString {
                append("📦 جميع الاصول المحملة:\n")
                append("📦 All Downloaded Assets:\n")
                append("━━━━━━━━━━━━━━━━━━\n\n")
                
                append("📁 Regular Assets: ${summary.regularAssetsCount}\n")
                append("   Size: ${summary.regularAssetsSize / 1024} KB\n\n")
                
                append("🔒 Encrypted Assets: ${summary.encryptedAssetsCount}\n")
                append("   Size: ${summary.encryptedAssetsSize / 1024} KB\n\n")
                
                append("🎴 Card Assets: ${summary.cardsCount}\n\n")
                
                append("━━━━━━━━━━━━━━━━━━\n")
                append("📊 Total Assets: ${summary.totalCount}\n")
                append("💾 Total Size: ${(summary.regularAssetsSize + summary.encryptedAssetsSize) / 1024} KB")
            }
            assetsListText.text = assetsList
        }
    }
}
