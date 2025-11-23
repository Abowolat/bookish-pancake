# bookish-pancake
KOBANI Asset Fetcher

## سحب جميع الاصول المشفرة والبطاقات الان (Pull All Encrypted Assets & Cards Now)

This Android application implements comprehensive asset fetching functionality with encryption support and card-based asset management.

## Features

- **Comprehensive Asset Pulling**: Download all types of assets (regular, encrypted, cards)
- **Encrypted Asset Storage**: AES-256 encryption for secure asset storage
- **Card-Based Assets**: Structured data assets with metadata support
- **Asset Management**: View, manage, and delete all asset types
- **Bilingual Interface**: Supports both Arabic and English
- **Progress Tracking**: Visual feedback during asset downloads
- **Batch Operations**: Fetch multiple assets at once
- **Security**: Automatic encryption key generation and management

## Components

### ComprehensiveAssetPuller
Orchestrates pulling of all asset types (regular, encrypted, and cards) in a unified operation.

### AssetFetcher
Fetches regular assets from remote URLs and saves them locally.

### EncryptedAssetFetcher
Downloads assets and encrypts them using AES-256 before storing locally. Manages encryption keys automatically.

### CardAssetFetcher
Manages card-based assets - structured data with metadata, categories, and content.

### AssetEncryption
Provides AES-256 encryption/decryption utilities for secure asset storage.

### MainActivity
User interface with bilingual support (Arabic/English) featuring:
- 🔄 سحب جميع الاصول الان (Pull All Assets Now) - Fetches regular, encrypted, and card assets
- 🗑️ مسح جميع الاصول (Clear All Assets) - Removes all downloaded assets

### AssetManager
Utility class for managing downloaded assets locally.

### AssetsConfig
Configuration defining default assets to fetch.

## Project Structure

```
app/
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/kobani/assetfetcher/
│   │   ├── MainActivity.kt                      # Main UI with comprehensive pulling
│   │   ├── ComprehensiveAssetPuller.kt         # Orchestrates all asset types
│   │   ├── AssetFetcher.kt                     # Regular asset fetching
│   │   ├── EncryptedAssetFetcher.kt            # Encrypted asset fetching
│   │   ├── CardAsset.kt                        # Card-based asset handling
│   │   ├── AssetEncryption.kt                  # AES-256 encryption utilities
│   │   ├── AssetManager.kt                     # Asset management utilities
│   │   └── AssetsConfig.kt                     # Asset configuration
│   └── res/layout/
│       └── activity_main.xml                    # Bilingual UI layout
```

## Asset Types

### 1. Regular Assets
Standard files downloaded from URLs and stored without encryption.

### 2. Encrypted Assets
Files downloaded and encrypted using AES-256 before storage. Encryption keys are automatically generated and securely stored.

### 3. Card Assets
Structured data assets containing:
- ID and title
- Content and category
- Source URL
- Custom metadata (key-value pairs)
- Stored as JSON files

## Security Features

### Encryption
- **Algorithm**: AES-256 in CBC mode with PKCS5 padding
- **Key Management**: Automatic generation and secure storage of encryption keys
- **Initialization Vector**: Random IV generated for each encryption operation
- **Key Storage**: Encryption keys stored securely in app's private storage

### Asset Security
- Encrypted assets are stored with `.enc` extension
- Decryption only performed on-demand
- Each app installation generates unique encryption keys
- Clear separation between regular and encrypted asset storage

## Building

```bash
gradle build
```

## Running

```bash
gradle installDebug
```

## Testing

```bash
gradle test
```

Tests cover:
- Asset encryption/decryption
- Card asset management
- Comprehensive pull operations
- Data integrity and validation

## Usage Example

```kotlin
// In your Activity or Fragment
val comprehensiveAssetPuller = ComprehensiveAssetPuller(context)

// Pull all assets (regular, encrypted, and cards)
lifecycleScope.launch {
    val result = comprehensiveAssetPuller.pullAllAssetsNow()
    
    if (result.isSuccess) {
        println("✓ Success: ${result.getSuccessCount()}")
        println("✗ Failed: ${result.getFailureCount()}")
        println("Total: ${result.getTotalCount()}")
    }
}

// Get summary of downloaded assets
val summary = comprehensiveAssetPuller.getAllAssetsSummary()
println("Regular assets: ${summary.regularAssetsCount}")
println("Encrypted assets: ${summary.encryptedAssetsCount}")
println("Card assets: ${summary.cardsCount}")

// Clear all assets
comprehensiveAssetPuller.clearAllAssets()
```

## Individual Asset Type Operations

### Encrypted Assets Only
```kotlin
val encryptedFetcher = EncryptedAssetFetcher(context)
val result = encryptedFetcher.fetchAndEncryptAsset(
    "https://example.com/asset.txt",
    "my_asset"
)

// Decrypt when needed
val decrypted = encryptedFetcher.decryptAsset("my_asset")
```

### Card Assets Only
```kotlin
val cardFetcher = CardAssetFetcher(context)
val card = CardAsset(
    id = "card_001",
    title = "My Card",
    content = "Card content",
    category = "Category",
    metadata = mapOf("key" to "value")
)
cardFetcher.saveCard(card)

// Load card
val loadedCard = cardFetcher.loadCard("card_001")
```

## Implementation Details

The app implements the requirement "سحب جميع الاصول مشفر بطاقات كل شي قم ب سحب الان" (Pull all encrypted assets, cards, everything. Pull now) through:

1. **Parallel Fetching**: All asset types are fetched simultaneously for optimal performance
2. **Type Safety**: Strongly-typed data classes for each asset type
3. **Error Handling**: Comprehensive error handling with detailed failure messages
4. **Bilingual Support**: Full Arabic and English interface support
5. **Separation of Concerns**: Each asset type has dedicated fetcher and storage 
