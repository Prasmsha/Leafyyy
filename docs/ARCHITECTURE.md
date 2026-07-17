# Leafly App Architecture

## MVVM Pattern
Model → Repository → ViewModel → UI (Composable)

## Package Structure
- model/ — Data classes (UserModel, PlantModel, CareTaskModel, GrowthLogModel)
- Repo/ — Repository interfaces and implementations
- ViewModel/ — ViewModels for each feature
- ui/theme/ — Colors, Typography, Theme

## Firebase Architecture
- Firebase Auth — User authentication
- Realtime Database — User profiles
- Firestore — Plants, Care Tasks, Growth Logs, Saved Tips

## Key Libraries
- Jetpack Compose — UI
- Navigation Compose — Screen navigation
- WorkManager — Background notifications
- OkHttp — Groq AI API calls
- Coil — Image loading
