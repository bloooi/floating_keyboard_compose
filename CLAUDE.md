# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **Floating Keyboard** Android application built with:
- **Kotlin** and **Jetpack Compose** for the UI
- **Android Gradle Plugin 8.12.3** with Kotlin 2.0.21
- **Minimum SDK**: 31, **Target SDK**: 36
- **Package**: `com.lee.floatingkeyboard`

## Common Development Commands

### Building
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK  
./gradlew assembleRelease

# Clean build
./gradlew clean
```

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run specific test
./gradlew testDebugUnitTest
```

### Code Quality
```bash
# Run lint checks
./gradlew lint

# Run lint and generate report
./gradlew lintDebug
```

### Installation and Running
```bash
# Install debug version on connected device
./gradlew installDebug

# Uninstall app
./gradlew uninstallDebug
```

## Architecture

### Project Structure
- **Main source**: `app/src/main/java/com/lee/floatingkeyboard/`
- **MainActivity.kt**: Single activity using Jetpack Compose
- **UI Theme**: Located in `ui/theme/` with Color.kt, Theme.kt, Type.kt
- **Tests**: Unit tests in `src/test/`, instrumented tests in `src/androidTest/`

### Key Dependencies (defined in gradle/libs.versions.toml)
- Jetpack Compose BOM 2024.09.00
- Material3 for UI components
- Activity Compose for Compose integration
- Core KTX and Lifecycle components

### Build Configuration
- Uses Gradle Version Catalogs (libs.versions.toml)
- Compose Compiler Plugin enabled
- Java 11 compatibility
- Edge-to-edge UI enabled