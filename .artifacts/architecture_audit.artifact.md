# RealtyNova Architecture Audit

## A. CURRENT ARCHITECTURE AUDIT

*   **UI Layer**: Jetpack Compose with Material 3. Navigation is type-safe using `androidx.navigation` with Kotlin Serialization.
*   **Presentation Layer**: MVVM pattern. ViewModels use `StateFlow` to expose UI state.
*   **Domain Layer**: Clean separation with `Repository` interfaces and plain data models (`Property`, `User`, `AuthState`).
*   **Data Layer**: `RepositoryImpl` handles data orchestration between Firestore and local Room cache (`PropertyDao`).
*   **DI**: Hilt (`@HiltAndroidApp`, `@InstallIn`).
*   **Firebase Integration**:
    *   **Auth**: Email/Password, Google, Facebook, Phone.
    *   **Firestore**: Primary database for properties, users, viewings, alerts.
    *   **Realtime Database**: Used for user presence (online/offline status).
    *   **Vertex AI**: Integrated via `GeminiManager` for property analysis.
*   **Local Storage**: Room database for property caching. `DataStore` (SessionManager) for user preferences/roles.

## B. CURRENT FEATURES

*   [x] **Authentication**: Multi-method login, registration, and onboarding.
*   [x] **Property Discovery**: Home screen with categories, featured property, and lazy-loaded listings.
*   [x] **AI Assistant**: Property evaluation and natural language interaction.
*   [x] **Saved Properties**: Basic favorite functionality.
*   [x] **Property Details**: Rich details including AI analysis, neighborhood intelligence, and action dock.
*   [x] **Scheduling**: Basic viewing request persistence in Firestore.
*   [x] **Market Pulse**: Market trend visualization (mocked).
*   [x] **Roles**: Buyer, Agent, Admin support.

## C. CURRENT PROBLEMS

1.  **Shallow Search**: Search is performed on a local list using simple string matching. No Firestore indexing for multi-criteria filtering (amenities, land size, price range).
2.  **Mocked Data**: Several high-value sections (Market Trends, some property details) rely on mock data or placeholders.
3.  **Placeholder Management**: Agent and Admin dashboards lack real-world functionality (moderation queue, listing management).
4.  **Security**: Firebase Security Rules are not defined within the codebase; document ownership is handled client-side in repositories.
5.  **Navigation Redundancy**: Some navigation logic is duplicated across screens.
6.  **Missing UX Feedback**: Verification workflows (Report listing, Verified badge verification) are visual but not functional.

## D. MISSING HIGH-VALUE FEATURES

1.  **Advanced Filtering**: Bed/Bath, Amenities, and Land Size filters.
2.  **Property Comparison**: Functional side-by-side analysis of two properties.
3.  **Lead Management**: Proper workflow for agents to see and update inquiry states.
4.  **Investor Analytics**: ROI, Yield, and Appreciation rate calculations for investment-focused buyers.
5.  **Natural Language Search**: Converting "3 bed house in Karen" into a structured Firestore query.
6.  **Trust Verification**: Reporting suspicious listings and verified identity badges.

## E. FEATURE PRIORITY MATRIX

| Feature | Value | Impact | Feasibility | Priority |
| :--- | :--- | :--- | :--- | :--- |
| Advanced Filtering | High | High | High | **Critical** |
| Lead Management | High | Medium | Medium | **High** |
| Property Comparison | Medium | High | High | **High** |
| Investor Analytics | High | Medium | Medium | **Medium** |
| NL Search | High | Medium | Low | **Low** |

## F. PROPOSED FIREBASE DATA MODEL

### Properties (Collection)
*   `id` (docId)
*   `ownerId` (string)
*   `status` (enum: PENDING, APPROVED, SOLD, ARCHIVED)
*   `trustScore` (object)
*   `searchKeywords` (array for basic search optimization)

### Inquiries (Collection)
*   `id` (docId)
*   `propertyId` (string)
*   `buyerId` (string)
*   `agentId` (string)
*   `status` (enum: NEW, CONTACTED, CLOSED)
*   `message` (string)

## G. SECURITY REQUIREMENTS

*   **Firestore Rules**: `allow update: if request.auth.uid == resource.data.ownerId`.
*   **Admin Access**: Restricted access to `moderationQueue` collection.
*   **Role Validation**: Server-side verification of `UserRole` before allowing listing approval.

## H. NAVIGATION IMPACT

*   No new routes required; will implement existing `PropertyComparison` and `AgentDashboard` sub-routes.
*   Improved `deepLink` handling for search results.

## I. UI/UX IMPROVEMENTS

*   **Filter Sheet**: A modern Material 3 `ModalBottomSheet` for property filtering.
*   **Comparison View**: Horizontal scrollable comparison table.
*   **Empty States**: Improved "No properties found" with "Reset Filters" action.

## J. IMPLEMENTATION ROADMAP

1.  **Phase 1 (Foundation)**: Robust Filtering & Search logic.
2.  **Phase 2 (Marketplace)**: Functional Agent/Admin Dashboards & Lead Management.
3.  **Phase 3 (Trust)**: Comparison System & Verification/Reporting tools.
4.  **Phase 4 (Intelligence)**: Investor Tools & NL Search refinement.

## K. RISKS

*   **Cost**: Deep Firestore queries with multiple filters can increase read costs; requires indexing.
*   **Data Integrity**: Inaccurate ROI/Yield calculations could mislead investors.
*   **Privacy**: Exposure of private seller coordinates (must use neighborhood centers).

## L. FEATURES TO REJECT (FOR NOW)

*   **M-Pesa Integration**: High compliance/legal overhead; postpone to Phase 5.
*   **360 Virtual Tours**: High bandwidth and storage requirement; keep as external link placeholder.
