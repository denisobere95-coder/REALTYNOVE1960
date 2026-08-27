# Connect to REALTYNOVAC3 Firebase Project

This plan outlines the steps to transition the project to a new Firebase environment named **REALTYNOVAC3**. To be creative and maintain flexibility, we will implement **Build Flavors**. This allows you to switch between the original project and the new C3 environment seamlessly.

## User Review Required

> [!IMPORTANT]
> You must download the official `google-services.json` for **REALTYNOVAC3** from the Firebase Console and replace the placeholder I will create. I will set up the structure, but the actual security keys must come from your Firebase Console.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle.kts](file:///C:/Users/denis/OneDrive/Downloads/REALTYNOVE1960-master/REALTYNOVE1960-master/app/build.gradle.kts)
- Define `productFlavors` for `classic` (original) and `c3` (REALTYNOVAC3).
- Configure unique application IDs if needed (e.g., `com.denis.realtynova.c3`).

### Project Structure

#### [MOVE] `app/google-services.json` -> `app/src/classic/google-services.json`
- Move the current configuration to the `classic` flavor directory.

#### [NEW] [google-services.json](file:///C:/Users/denis/OneDrive/Downloads/REALTYNOVE1960-master/REALTYNOVE1960-master/app/src/c3/google-services.json)
- Create a template `google-services.json` for the `REALTYNOVAC3` project.

### Code Improvements

#### [MODIFY] [AuthRepositoryImpl.kt](file:///C:/Users/denis/OneDrive/Downloads/REALTYNOVE1960-master/REALTYNOVE1960-master/app/src/main/java/com/denis/realtynova/core/data/repository/AuthRepositoryImpl.kt)
- Update the configuration check to be more descriptive about which Firebase environment is active.
- Add a "Creative" welcome log or analytics event for the C3 environment.

## Verification Plan

### Automated Tests
- Run `gradlew assembleC3Debug` to ensure the new flavor builds correctly.

### Manual Verification
- Switch to the `c3Debug` build variant in Android Studio.
- Verify that the app attempts to connect to the new project (logs will show the new project ID).
