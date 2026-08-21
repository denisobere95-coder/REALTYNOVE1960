# REALTYNOVA Architecture Document

## 1. Package Structure
Following Clean Architecture + MVVM, organized by feature.

```
com.denis.realtynova
├── core
│   ├── common          # String extensions, constants, etc.
│   ├── data            # Common data sources, network clients
│   ├── designsystem    # M3 Theme, reusable components
│   ├── di              # Global DI modules
│   ├── domain          # Common models and use cases
│   └── network         # Retrofit config, interceptors
├── features
│   ├── auth            # Login, Registration, OTP
│   ├── properties      # Search, Listing, Details
│   ├── profile         # User info, Settings
│   └── map             # Google Maps integration
├── MainActivity.kt
└── RealtyNovaApp.kt     # Application class
```

## 2. Module Structure
For the initial phase, a single `:app` module with internal package separation. As the project grows, it will be refactored into:
- `:core:ui`
- `:core:data`
- `:core:domain`
- `:feature:auth`
- `:feature:properties`
- etc.

## 3. Dependency Graph
- UI -> ViewModel -> UseCase -> Repository -> DataStore/Room/Retrofit

## 4. Naming Conventions
- **Classes**: PascalCase (e.g., `PropertyRepository`)
- **Functions**: camelCase (e.g., `getPropertyById`)
- **Composables**: PascalCase (e.g., `PropertyCard`)
- **Resources**: snake_case (e.g., `ic_home`)
- **State**: `[Feature]UiState` (e.g., `PropertyDetailUiState`)

## 5. State-Management Strategy
- **Unidirectional Data Flow (UDF)**:
  - ViewModel exposes a single `StateFlow<UiState>`.
  - UI observes the state and sends `Events` to the ViewModel.
- **UI State**: Sealed classes/interfaces for Loading, Success, Error, Empty.

## 6. Navigation Strategy
- **Type-safe Navigation Compose**: Using Kotlin Serialization for route definitions.
- **NavHost**: Defined in `MainActivity` or a top-level `RealtyNovaApp` Composable.

## 7. Networking Strategy
- **Retrofit + OkHttp**: Single instance of OkHttpClient with interceptors for logging and auth headers.
- **Serialization**: Kotlinx Serialization for JSON parsing.
- **Error Handling**: `Result` wrapper or custom `Resource` sealed class.

## 8. Database Strategy
- **Room**: Local caching for properties and user data.
- **Offline-first**: Repositories act as mediators between Remote and Local data sources.

## 9. Authentication Strategy
- **Firebase Auth** (or custom JWT via Retrofit).
- **Token Management**: Refresh tokens handled by OkHttp `Authenticator`.
- **Encrypted Storage**: Credentials stored in `EncryptedSharedPreferences` or `DataStore`.

## 10. Error-Handling Strategy
- Centralized error handling using a `Result` wrapper.
- UI mapping of technical errors (e.g., `SocketTimeoutException`) to user-friendly messages.

## 11. Logging Strategy
- **Timber**: For debug logging.
- **Firebase Crashlytics**: For production error reporting.

## 12. Testing Strategy
- **Unit Tests**: JUnit 5 + MockK for ViewModels and Repositories.
- **UI Tests**: Compose Testing library for critical flows.
- **Hilt**: Used for dependency injection in tests.

## 13. Build Configuration Strategy
- **Build Variants**: `debug`, `staging`, `release`.
- **Secrets Management**: `secrets.properties` (not in VCS) + BuildConfig.

## 14. Environment Configuration
- Environment-specific URLs and API keys managed via `gradle.properties` or `local.properties`.

## 15. API Abstraction
- Repositories expose Domain models, hiding Network/Database entities.

## 16. Offline-first Strategy
- Use Room as the single source of truth for the UI where applicable.
- WorkManager for syncing background data.

## 17. Caching Strategy
- **Images**: Coil with disk and memory cache.
- **Data**: Room with TTL for expired entries.

## 18. Pagination Strategy
- **Paging 3**: For large lists of properties.

## 19. Image-Loading Strategy
- **Coil**: Optimized for Compose, handles vector drawables and remote URLs.

## 20. Analytics Strategy
- **Firebase Analytics**: Track user events (Search, Property View, Contact Agent).

## 21. Crash-Reporting Strategy
- **Firebase Crashlytics**: Automatic crash reporting with custom keys for context.
