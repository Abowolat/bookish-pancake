# GitHub Copilot Instructions for KOBANI Asset Fetcher

## Project Overview

This is an Android application called "KOBANI Asset Fetcher" (bookish-pancake) that implements asset fetching functionality. The app downloads assets from remote URLs, manages them locally, and provides a bilingual interface (Arabic and English).

## Project Structure

- **Language**: Kotlin
- **Build System**: Gradle
- **Target Platform**: Android (minSdk 21, targetSdk 33)
- **Key Components**:
  - `MainActivity.kt`: UI that demonstrates asset fetching functionality
  - `AssetFetcher.kt`: Core class for downloading assets from remote URLs
  - `AssetManager.kt`: Utility for managing downloaded assets
  - `AssetsConfig.kt`: Configuration defining which assets to fetch

## Code Style and Conventions

### Kotlin Style
- Use standard Kotlin conventions and idiomatic Kotlin code
- Prefer data classes for simple data holders
- Use coroutines for asynchronous operations (already using `kotlinx-coroutines-android`)
- Keep functions focused and single-purpose

### Naming Conventions
- Classes: PascalCase (e.g., `AssetFetcher`, `MainActivity`)
- Functions: camelCase (e.g., `fetchAssets`, `clearDownloadedAssets`)
- Constants: UPPER_SNAKE_CASE (e.g., `BUFFER_SIZE`, `TAG`)
- Use descriptive names that clearly indicate purpose

### Comments
- Include bilingual comments when appropriate (Arabic and English)
- The app implements the requirement: "قم ب سحب الاصول الان" (Pull/Fetch the assets now)
- Maintain existing bilingual UI text patterns
- Use KDoc format for public APIs and classes

### Coroutines
- Use `Dispatchers.IO` for I/O operations (network, file operations)
- Use `Dispatchers.Main` for UI updates
- Handle exceptions properly with try-catch blocks
- Use `withContext` for switching dispatchers

## Testing

### Test Framework
- JUnit 4 (`junit:junit:4.13.2`) for unit tests
- `kotlinx-coroutines-test` for testing coroutines
- Tests are located in `app/src/test/java/com/kobani/assetfetcher/`

### Testing Guidelines
- Write unit tests for utility functions and business logic
- Test validation functions (e.g., `isValidAssetUrl`, `isValidFileName`)
- Verify data class behavior
- Test edge cases and error conditions
- Keep tests focused on a single responsibility

## Building and Testing

### Build Commands
```bash
./gradlew build
```

### Run Tests
```bash
./gradlew test
```

### Install Debug APK
```bash
./gradlew installDebug
```

## Dependencies

### Core Dependencies
- `androidx.core:core-ktx:1.9.0`
- `androidx.appcompat:appcompat:1.6.0`
- `com.google.android.material:material:1.8.0`
- `androidx.constraintlayout:constraintlayout:2.1.4`
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4`

### Testing Dependencies
- `junit:junit:4.13.2`
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4`

## Important Guidelines

### When Working with Existing Code
- Maintain the bilingual nature of the app (Arabic and English)
- Preserve existing UI patterns and user experience
- Keep the asynchronous nature of asset fetching operations
- Don't break existing functionality when adding new features

### Error Handling
- Always handle network errors gracefully
- Provide meaningful error messages in both Arabic and English
- Log errors using Android's Log class with appropriate tags
- Use Result types for operations that may fail

### File Operations
- Downloaded assets are stored in `context.filesDir/downloaded_assets`
- Always check if directories exist before file operations
- Close streams properly (use `.use` for automatic resource management)
- Use appropriate buffer sizes for file I/O (current: 8192 bytes)

### Network Operations
- Set reasonable timeouts (current: 10000ms for connect and read)
- Only support HTTP and HTTPS protocols
- Always run network operations on IO dispatcher
- Disconnect connections in finally blocks

## Security Considerations

- Validate URLs before fetching (only allow http:// and https://)
- Validate file names to prevent path traversal attacks
- Don't store sensitive data in downloaded assets
- Be cautious with file permissions

## UI/UX Considerations

- Maintain bilingual text (Arabic first, then English)
- Show progress indicators for long-running operations
- Disable action buttons during operations to prevent multiple submissions
- Provide clear feedback on operation success or failure
- Display file sizes in human-readable format (KB)

## Additional Notes

- The root project name is "AssetFetcher" (in `settings.gradle`)
- The app display name is "KOBANI Asset Fetcher" (in `AndroidManifest.xml`)
- The package name is `com.kobani.assetfetcher`
- Kotlin version: 1.8.0
- Android Gradle Plugin: 7.4.0
- Java compatibility: 1.8
