# Implementation Plan - Fix Build and Code Errors

This plan addresses several technical issues and inconsistencies found in the project, including invalid SDK versions, redundant navigation files, and non-idiomatic navigation checks.

## User Review Required

> [!IMPORTANT]
> - `compileSdk` and `targetSdk` are being downgraded from 37 to 35. API 37 is not yet available in stable Android releases.
> - Android Gradle Plugin (AGP) is being downgraded from 9.3.1 to 8.7.2 to match stable release patterns.
> - Redundant navigation files (`NavGraph.kt`, `AppNovHost.kt`) will be deleted.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/gradle/libs.versions.toml)
- Update `agp` version to `8.7.2`.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/build.gradle.kts)
- Change `compileSdk` and `targetSdk` to `35`.

### Navigation Cleanup

#### [DELETE] [NavGraph.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/core/navigation/NavGraph.kt)
- This file is redundant and uses old route definitions.

#### [DELETE] [AppNovHost.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/core/navigation/AppNovHost.kt)
- This file is empty.

### Code Improvements

#### [MODIFY] [MainActivity.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/MainActivity.kt)
- Refactor `showBottomBar` and `selectedIndex` to use type-safe `hasRoute` checks from Jetpack Navigation 2.8.
- Add missing trailing comma as suggested by analysis.

## Verification Plan

### Automated Tests
- Run `./gradlew assembleDebug` to ensure build succeeds with corrected versions.

### Manual Verification
- Deploy the app to a device/emulator to verify that navigation and the bottom bar work as expected.
