# Deepfake Detector Android App

A modern Android application built with **Kotlin** and **Jetpack Compose** for detecting deepfakes in images, videos, and audio files using AI.

## Features

- 🖼️ **Image Analysis**: Upload and analyze images for deepfake detection
- 🎬 **Video Analysis**: Detect deepfakes in video files
- 🎵 **Audio Analysis**: Analyze audio files for synthetic voice detection
- 🎨 **Modern UI**: Material3 design with dark/light theme support
- 📱 **Clean Architecture**: MVVM pattern with Hilt dependency injection
- 🌐 **API Integration**: Connects to Hugging Face Space for AI predictions

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material3
- **Architecture**: Clean Architecture + MVVM
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Async**: Kotlin Coroutines + Flow

## Project Structure

```
app/
├── data/
│   ├── model/          # Data models and sealed classes
│   ├── remote/         # API service interfaces
│   └── repository/     # Repository implementations
├── domain/
│   └── usecase/        # Business logic use cases
├── ui/
│   ├── screen/         # Compose screens
│   ├── theme/          # Material3 theme configuration
│   └── viewmodel/      # ViewModels for state management
└── di/                 # Dependency injection modules
```

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Minimum Android version: 7.0 (API 24)

### Installation

1. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the `DeepfakeDetectorApp` folder
   - Click "OK"

2. **Sync Gradle**:
   - Android Studio will automatically start syncing Gradle
   - Wait for the sync to complete
   - If prompted, accept any SDK or tool updates

3. **Build the Project**:
   - Click "Build" → "Make Project" or press `Ctrl+F9` (Windows) / `Cmd+F9` (Mac)
   - Wait for the build to complete

4. **Run the App**:
   - Connect an Android device or start an emulator
   - Click the "Run" button or press `Shift+F10`
   - Select your device/emulator
   - The app will install and launch

## Configuration

### API Endpoint

The app connects to the Hugging Face Space API by default:
- **Base URL**: `https://praneshjs-fakevideodetect.hf.space/`

To use a local backend:
1. Open `ApiService.kt`
2. Change `BASE_URL` to:
   - `http://10.0.2.2:5000/` for Android Emulator
   - `http://YOUR_LOCAL_IP:5000/` for physical device

### Permissions

The app requires the following permissions (automatically requested):
- `INTERNET`: For API calls
- `READ_MEDIA_IMAGES`: To access images (Android 13+)
- `READ_MEDIA_VIDEO`: To access videos (Android 13+)
- `READ_MEDIA_AUDIO`: To access audio files (Android 13+)
- `READ_EXTERNAL_STORAGE`: For older Android versions

## Usage

1. **Select Media Type**: Choose from Image, Video, or Audio tabs
2. **Upload File**: Tap "Select [Type]" button to choose a file
3. **Wait for Analysis**: The app will upload and analyze the file
4. **View Results**: Results will display with confidence scores and detection status

## Troubleshooting

### Gradle Sync Issues

If you encounter Gradle sync errors:

1. **Check JDK Version**:
   - Go to File → Project Structure → SDK Location
   - Ensure JDK 17 is selected

2. **Clear Cache**:
   - File → Invalidate Caches → Invalidate and Restart

3. **Update Gradle**:
   - Update Gradle wrapper if prompted
   - Sync again

### Build Errors

If you get build errors:

1. **Clean and Rebuild**:
   - Build → Clean Project
   - Build → Rebuild Project

2. **Check Dependencies**:
   - Ensure all dependencies in `build.gradle.kts` are accessible
   - Check internet connection for dependency downloads

### Runtime Issues

If the app crashes or doesn't work:

1. **Check Permissions**: Ensure all permissions are granted in device settings
2. **Network Connection**: Verify internet connectivity
3. **API Availability**: Check if the Hugging Face Space is accessible

## API Response Format

The app expects responses in this format:

```json
{
  "result": "The image is FAKE. Confidence score is: 85.3%",
  "status": "success",
  "source": "huggingface_space",
  "file_info": {
    "name": "image.jpg",
    "size": 1024000,
    "size_mb": 1.0,
    "size_kb": 1000.0
  }
}
```

## Architecture Details

### Data Flow

1. User selects file → `MainScreen`
2. URI passed to → `MainViewModel`
3. ViewModel calls → `AnalyzeMediaUseCase`
4. Use case invokes → `DeepfakeRepository`
5. Repository converts URI to file and calls → `ApiService`
6. Response flows back through layers
7. UI updates with → `StateFlow`

### State Management

- Uses Kotlin `StateFlow` for reactive UI updates
- Sealed class `AnalysisResult` for type-safe state handling
- Separate state flows for each media type (image, video, audio)

## License

This project is part of the AI-Generated-Video-Detector system.

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review the implementation plan
3. Check Android Studio's Build Output for specific errors
