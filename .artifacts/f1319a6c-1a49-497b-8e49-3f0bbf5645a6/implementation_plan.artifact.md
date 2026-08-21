# REALTYNOVA Full Development Plan

This plan outlines the steps to transition REALTYNOVA from a UI prototype with dummy data to a fully functional production-ready application addressing the core pillars of modern Android development.

## User Review Required

> [!IMPORTANT]
> **Firestore Migration:** All property data will move to Firebase Firestore. This requires setting up a Firestore project and seeding it with initial data.
> **AI Assistant:** Real AI integration will require a Gemini API key or Vertex AI setup.
> **Image Uploads:** Property posting will require Firebase Storage bucket configuration.

## Proposed Changes

---

### Phase 1: Persistence & Infrastructure
Transition from hardcoded data to a scalable cloud backend.

#### [MODIFY] [PropertyRepositoryImpl.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/core/data/repository/PropertyRepositoryImpl.kt)
- Replace hardcoded list with Firestore collection calls.
- Implement real `searchProperties` using Firestore query operators.

#### [NEW] [SavedRepositoryImpl.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/core/data/repository/SavedRepositoryImpl.kt)
- Implement `SavedRepository` using a "Saved" collection in Firestore or a local Room database for offline support.

---

### Phase 2: Core Feature Wiring
Making the UI functional with real data.

#### [MODIFY] [SearchScreen.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/features/search/SearchScreen.kt)
- Connect search query and filters to a new `SearchViewModel`.
- Implement state handling for search results.

#### [MODIFY] [ProfileScreen.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/features/profile/ProfileScreen.kt)
- Design and implement the full profile UI:
    - User avatar and info (from `AuthViewModel`).
    - Account settings (Role toggle, Password reset).
    - "My Listings" section (for Agents).
    - Logout functionality.

#### [NEW] [PropertyPostScreen.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/features/home/PropertyPostScreen.kt)
- Create a multi-step form for Agents to post new properties.
- Integrate **Photo Picker API** and **Firebase Storage** for image uploads.

---

### Phase 3: Intelligent & Real-time Features
Elevating the user experience with AI and notifications.

#### [MODIFY] [AiAssistantViewModel.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/features/search/AiAssistantViewModel.kt)
- Integrate **Google Generative AI SDK (Gemini)**.
- Implement "Property Injection" so the AI can suggest real properties from the Firestore database.

#### [NEW] [RealtyNovaMessagingService.kt](file:///C:/Users/denis/AndroidStudioProjects/REALTYNOVA/app/src/main/java/com/denis/realtynova/core/notifications/RealtyNovaMessagingService.kt)
- Setup **Firebase Cloud Messaging (FCM)** for push notifications (New listings, AI alerts).

## Verification Plan

### Automated Tests
- `PropertyRepositoryTest`: Verify Firestore fetch and search logic.
- `AuthIntegrationTest`: Verify role-based access and profile updates.

### Manual Verification
- **Auth Flow:** Test Google/Phone/Email login and role selection.
- **Posting Flow:** Post a property with images and verify it appears in the Home/Search feeds.
- **AI Chat:** Interact with the AI and ensure it returns valid property suggestions.
- **Maps:** Verify marker clusters and property detail navigation from the map.
