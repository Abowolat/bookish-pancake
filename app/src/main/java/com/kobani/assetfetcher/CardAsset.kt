package com.kobani.assetfetcher

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * CardAsset - Represents a card-based asset
 * "بطاقة الاصول" (Asset Card)
 * 
 * Cards are structured data assets that can contain metadata, content, and references
 */
data class CardAsset(
    val id: String,
    val title: String,
    val content: String,
    val category: String = "",
    val url: String = "",
    val metadata: Map<String, String> = emptyMap(),
    val isEncrypted: Boolean = false
) {
    /**
     * Convert card to JSON string
     */
    fun toJson(): String {
        val json = JSONObject().apply {
            put("id", id)
            put("title", title)
            put("content", content)
            put("category", category)
            put("url", url)
            put("isEncrypted", isEncrypted)
            
            val metadataJson = JSONObject()
            metadata.forEach { (key, value) ->
                metadataJson.put(key, value)
            }
            put("metadata", metadataJson)
        }
        return json.toString()
    }

    companion object {
        /**
         * Parse card from JSON string
         */
        fun fromJson(jsonString: String): CardAsset {
            val json = JSONObject(jsonString)
            val metadataJson = json.optJSONObject("metadata")
            val metadata = mutableMapOf<String, String>()
            
            metadataJson?.let {
                it.keys().forEach { key ->
                    metadata[key] = it.getString(key)
                }
            }
            
            return CardAsset(
                id = json.getString("id"),
                title = json.getString("title"),
                content = json.getString("content"),
                category = json.optString("category", ""),
                url = json.optString("url", ""),
                metadata = metadata,
                isEncrypted = json.optBoolean("isEncrypted", false)
            )
        }
    }
}

/**
 * CardAssetFetcher - Fetches and manages card-based assets
 * "سحب بطاقات الاصول" (Pull asset cards)
 */
class CardAssetFetcher(private val context: Context) {

    companion object {
        private const val TAG = "CardAssetFetcher"
        private const val CARDS_DIR = "asset_cards"
    }

    private val cardsDirectory: File
        get() = File(context.filesDir, CARDS_DIR).also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

    /**
     * Fetch card assets from URLs and save them
     */
    suspend fun fetchCards(cards: List<CardAsset>): List<CardFetchResult> {
        return cards.map { card ->
            try {
                saveCard(card)
                CardFetchResult(
                    card = card,
                    isSuccess = true,
                    error = null
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching card ${card.id}: ${e.message}", e)
                CardFetchResult(
                    card = card,
                    isSuccess = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Save a card to local storage
     */
    fun saveCard(card: CardAsset): File {
        val file = File(cardsDirectory, "${card.id}.json")
        file.writeText(card.toJson())
        Log.d(TAG, "Saved card: ${card.id}")
        return file
    }

    /**
     * Load a card from local storage
     */
    fun loadCard(cardId: String): CardAsset? {
        val file = File(cardsDirectory, "$cardId.json")
        return if (file.exists()) {
            try {
                val jsonString = file.readText()
                CardAsset.fromJson(jsonString)
            } catch (e: Exception) {
                Log.e(TAG, "Error loading card $cardId: ${e.message}", e)
                null
            }
        } else {
            null
        }
    }

    /**
     * Get all saved cards
     */
    fun getAllCards(): List<CardAsset> {
        return cardsDirectory.listFiles()?.mapNotNull { file ->
            if (file.extension == "json") {
                try {
                    CardAsset.fromJson(file.readText())
                } catch (e: Exception) {
                    Log.e(TAG, "Error reading card file ${file.name}: ${e.message}", e)
                    null
                }
            } else {
                null
            }
        } ?: emptyList()
    }

    /**
     * Delete a specific card
     */
    fun deleteCard(cardId: String): Boolean {
        val file = File(cardsDirectory, "$cardId.json")
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    /**
     * Delete all cards
     */
    fun deleteAllCards(): Boolean {
        return try {
            cardsDirectory.deleteRecursively()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting all cards: ${e.message}", e)
            false
        }
    }

    /**
     * Get card count
     */
    fun getCardCount(): Int {
        return cardsDirectory.listFiles()?.count { it.extension == "json" } ?: 0
    }
}

/**
 * Result of card fetch operation
 */
data class CardFetchResult(
    val card: CardAsset,
    val isSuccess: Boolean,
    val error: String? = null
)
