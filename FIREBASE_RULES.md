# RealtyNova Firebase Security Rules

To ensure your backend is secure and functions perfectly, apply these rules in your Firebase Console.

## 🛡️ Cloud Firestore Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // User Profiles: Users can only read/write their own profile
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Properties: Anyone can read, only authenticated users (Agents/Admins) can write
    match /properties/{propertyId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Viewings: Users can see their own viewings, Agents can see viewings for their properties
    match /viewings/{viewingId} {
      allow create: if request.auth != null;
      allow read: if request.auth != null;
    }
    
    // Search Alerts
    match /searchAlerts/{alertId} {
      allow create, read: if request.auth != null;
    }
  }
}
```

## 💬 Realtime Database Rules (Chat)
```json
{
  "rules": {
    "messages": {
      "$chatId": {
        ".read": "auth != null && $chatId.contains(auth.uid)",
        ".write": "auth != null && $chatId.contains(auth.uid)"
      }
    },
    "chats": {
      "$userId": {
        ".read": "auth != null && auth.uid == $userId",
        ".write": "auth != null && auth.uid == $userId"
      }
    },
    "typing": {
      "$chatId": {
        ".read": "auth != null && $chatId.contains(auth.uid)",
        ".write": "auth != null && $chatId.contains(auth.uid)"
      }
    }
  }
}
```

## 📁 Firebase Storage Rules
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Property Images: Public read, authenticated write
    match /properties/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null;
    }
    
    // Profile Pictures
    match /profile_pictures/{userId}/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

---
Apply these in the respective tabs in your [Firebase Console](https://console.firebase.google.com/).
