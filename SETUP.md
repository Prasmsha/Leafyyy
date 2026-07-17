# Leafly Android - Setup Instructions

## Required Setup Before Running

### 1. Firebase Authentication
- Enable Email/Password in Firebase Console → Authentication

### 2. Firebase Realtime Database (Required by assignment)
- Enable Realtime Database in Firebase Console
- Set Rules to allow authenticated users:
```json
{
  "rules": {
    ".read": "auth != null",
    ".write": "auth != null"
  }
}
```

### 3. Cloud Firestore (For complex queries)
- Enable Firestore in Firebase Console
- Set Rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 4. Groq API Key (Required for AI Plant Care)
- Go to https://console.groq.com/keys
- Create a new API key
- Replace `YOUR_GROQ_API_KEY_HERE` in:
  - `Dashboard.kt` (AI bot widget)
  - `AiPlantCareScreen.kt` (AI chat screen)

## Database Architecture
| Data | Database | Reason |
|---|---|---|
| User profiles | Firebase Realtime Database | Assignment requirement |
| Plants | Both (Firestore + Realtime DB) | Real-time sync + queries |
| Care Tasks | Firestore | Complex queries |
| Growth Logs | Firestore | Complex queries |

## Features
| Feature | Status |
|---|---|
| Splash / Login / Register / Forgot Password | ✅ |
| Firebase Authentication | ✅ |
| Firebase Realtime Database (users + plants) | ✅ |
| Firebase Firestore (tasks + logs) | ✅ |
| Dashboard with real data + AI bot | ✅ |
| Plant CRUD with search | ✅ |
| Edit Plant | ✅ |
| Care Tasks per plant | ✅ |
| Growth Logs per plant | ✅ |
| Watering Reminders (push notifications) | ✅ |
| Profile with photo (free, no Storage needed) | ✅ |
| Dark / Light Theme Toggle | ✅ |
| CSV Export | ✅ |
| Saved Tips | ✅ |
| AI Plant Care Tips (Groq Llama 3.3) | ✅ |
| Reminders Screen | ✅ |
