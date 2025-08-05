# QuestPod

An interactive AI-generated audio storytelling app where users verbally guide adventures through immersive narratives.

## Overview

QuestPod is a cross-platform mobile application that leverages AI to create dynamic, interactive storytelling experiences. Users can verbally interact with stories, making choices that branch the narrative in different directions. The app features high-quality neural voice synthesis, personalized content, and social sharing capabilities.

### Key Features

- **Interactive Storytelling**: Navigate through branching narratives with voice commands
- **AI-Generated Content**: Dynamic story generation powered by advanced language models
- **Neural Voice Synthesis**: High-quality voice narration with character-specific voices
- **Progress Tracking**: Resume stories exactly where you left off
- **Social Features**: Share highlights and discuss stories with other users
- **Personalization**: Content adapts to user preferences and reading levels
- **Educational Elements**: Optional learning content embedded within stories

## Technologies

- **Kotlin Multiplatform** with Jetpack Compose UI for cross-platform development
- **Supabase** for backend database, authentication, and storage
- **a0.dev/Manus** for AI story generation
- **ElevenLabs** for neural voice synthesis
- **RevenueCat** for subscription management
- **OneSignal** for engagement notifications
- **Koin** for dependency injection
- **Ktor** for networking

## Architecture

QuestPod follows clean architecture principles with the following package structure:

- **com.questpod.data**: Repositories and remote data sources
- **com.questpod.domain**: Models and use cases
- **com.questpod.presentation**: ViewModels and Composable UI components
- **com.questpod.util**: Helper functions and utilities

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer
- Xcode 13 or newer (for iOS development)
- JDK 17 or newer
- Kotlin 1.9.0 or newer

### Setup

1. Clone the repository
   ```
   git clone https://github.com/yourusername/questpod.git
   ```

2. Open the project in Android Studio

3. Create a `local.properties` file in the project root with the following properties:
   ```
   supabase.url=YOUR_SUPABASE_URL
   supabase.key=YOUR_SUPABASE_KEY
   elevenlabs.key=YOUR_ELEVENLABS_API_KEY
   ```

4. Sync the project with Gradle files

5. Run the app on your preferred device/emulator

## Navigation

QuestPod uses Jetpack Compose Navigation for screen navigation. The main navigation graph includes:

- Home Screen: Discover new stories and continue reading
- Story Detail: View story information before starting
- Story Player: Interactive story playback with voice controls
- Profile: User settings and preferences
- Library: User's saved and in-progress stories

## Database

QuestPod uses Supabase PostgreSQL database with a robust schema design. See [database.md](database.md) for detailed information about the database structure, including:

- Table schemas
- Row-Level Security policies
- Indexes and performance optimizations
- Soft delete implementation
- Admin functionality

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
