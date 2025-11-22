# bookish-pancake
KOBANI Asset Fetcher

## قم ب سحب الاصول الان (Pull/Fetch the Assets Now)

This Android application implements asset fetching functionality as requested.

## Features

- **Asset Fetching**: Download assets from remote URLs
- **Asset Management**: View, manage, and delete downloaded assets
- **Bilingual Interface**: Supports both Arabic and English
- **Progress Tracking**: Visual feedback during asset downloads
- **Batch Operations**: Fetch multiple assets at once

## Components

### AssetFetcher
Main class responsible for fetching/downloading assets from remote URLs.

### MainActivity
User interface that demonstrates the asset fetching functionality with buttons to:
- سحب الاصول الان (Fetch Assets Now)
- مسح الاصول (Clear Assets)

### AssetManager
Utility class for managing downloaded assets locally.

### AssetsConfig
Configuration file defining which assets should be fetched.

## Project Structure

```
app/
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/kobani/assetfetcher/
│   │   ├── MainActivity.kt
│   │   ├── AssetFetcher.kt
│   │   ├── AssetManager.kt
│   │   └── AssetsConfig.kt
│   └── res/layout/
│       └── activity_main.xml
```

## Building

```bash
./gradlew build
```

## Running

```bash
./gradlew installDebug
``` 
