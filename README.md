# REALTYNOVA

A luxury property concierge and marketplace for Kenyan real estate.

## Features
- **AI Property Assistant**: Curated property suggestions powered by Gemini.
- **Real-time Chat**: Connect with property agents instantly via Firebase Realtime Database (includes **Typing Indicators**).
- **Luxury UI**: Premium design language featuring Emerald, Gold, and Navy tones with **Dark Mode** support.
- **Property Maps**: Integrated Google Maps with **Premium Styling**, directions, and coordinate discovery.
- **Identity Verification**: Secure authentication, Firestore user profiles, and verified user badges.

## 🛡️ Security
Backend security rules for Firestore, Realtime Database, and Storage are documented in [FIREBASE_RULES.md](FIREBASE_RULES.md).

## Tech Stack
- **Kotlin & Jetpack Compose**: Modern Android development.
- **Hilt**: Dependency injection.
- **Retrofit & OkHttp**: Networking.
- **Firebase**:
  - Authentication
  - Firestore (Property data)
  - Realtime Database (Chat & Messages)
  - Storage (Property images)
  - Vertex AI (Gemini integration)
- **Room**: Local data persistence.

## Getting Started
1. Clone the repository.
2. Add your `google-services.json` to the `app/` directory.
3. Add your Maps API Key to `local.properties`: `maps.api.key=YOUR_KEY`.
4. Build and run.

---
Developed by **Denis Obere**
