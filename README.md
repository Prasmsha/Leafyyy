# Leafly 🌱 - Plant Care Android App

A plant care tracking app built with Kotlin, Jetpack Compose, Firebase and Groq AI.

## Student Info
- Module: SP5000COM Android Application Development with Kotlin
- Due: July 26, 2026

## Features
- Firebase Authentication (Email/Password + Google Sign-In)
- Plant CRUD with image upload (stored free in Firestore)
- Care Tasks per plant (add/check/delete)
- Growth Logs per plant
- AI Plant Care Tips (Groq Llama 3.3)
- Watering Reminders (local notifications)
- Dashboard with real-time plant data
- Dark/Light Theme Toggle
- CSV Export
- Saved Tips from AI
- Delete Account

## Tech Stack
- Kotlin + Jetpack Compose
- Firebase Auth + Realtime Database + Firestore
- MVVM Architecture (Model → Repo → ViewModel)
- WorkManager for notifications
- OkHttp for Groq API
- Jetpack Navigation Compose

## Setup
1. Add your google-services.json to app/ folder
2. Add your Groq API key in Constants.kt
3. Enable Firebase Auth, Realtime Database and Firestore
4. Set Firestore rules to allow authenticated users
5. Run the app!

## Testing
- Unit tests: src/test/
- Instrumented tests: src/androidTest/
