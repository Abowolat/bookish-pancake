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

## Building

```bash
./gradlew build
```

## Running

```bash
./gradlew installDebug
``` 
