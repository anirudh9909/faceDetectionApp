# Face Detection App

This Android application detects faces in images and allows users to tag each face individually. The app leverages Google ML Kit's Face Detection API to identify faces and supports unique tagging for each detected face within images. This app is structured using the MVVM (Model-View-ViewModel) architecture, ensuring modularity, maintainability, and testability.

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture, providing a clear separation of responsibilities:

- **Model Layer**: Contains the data repository, `FaceDetectionRepository`, which loads images from device storage and uses Google ML Kit to detect faces within each image.
- **ViewModel Layer**: The `FaceDetectionViewModel` manages the UI-related data in LiveData objects and communicates with the repository to handle face detection asynchronously.
- **View Layer**: `MainActivity` is responsible for UI setup, managing the RecyclerView to display images with detected faces, and handling tagging functionality. `FaceImageAdapter` draws bounding boxes and tags on each detected face.
- **Utility Layer**: `PermissionUtils` manages permission requests and results, abstracting permission logic from the activity for better modularity.

## Features

- **Face Detection**: Detects faces in images using Google ML Kit's Face Detection API.
- **Tagging**: Users can click on a face to add a custom tag, which will be displayed above the face's bounding box in the image.
- **Modular Permission Handling**: Uses `PermissionUtils` to handle permission requests based on Android version, ensuring compatibility with Android 6 to Android 13+.
- **Progress Indicator**: Shows a progress bar while loading images and detecting faces.

## Assumptions

- **Unique Face Identifiers**: Each face is identified by its unique ID (`trackingId` or `hashCode`) for tagging purposes.
- **Permissions**: The app requests storage permissions based on the Android version:
  - Android 13+: Requests `READ_MEDIA_IMAGES`.
  - Android 11-12: Requests `MANAGE_EXTERNAL_STORAGE`.
  - Android 6-10: Requests `READ_EXTERNAL_STORAGE`.
- **Storage Access**: Assumes the device has image files available and that storage permissions are granted.

## Libraries Used

- **Google ML Kit (Face Detection)**: Used to detect faces in images.
- **AndroidX Libraries**: Includes RecyclerView, LiveData, ViewModel, and Lifecycle.
- **Coroutines**: For asynchronous operations in the `FaceDetectionViewModel`.

## Instructions to Run the App

### Prerequisites

- **Android Studio**: Ensure Android Studio is installed (version 4.0+ recommended).
- **Android Device or Emulator**: An Android device or emulator running Android 6.0 (API level 23) or higher.
- **Internet Connection**: Not required, but storage permissions are needed to access images.

### Steps to Run

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd face-detection-app

### Open in Android Studio

1. Open Android Studio and select **"Open an existing Android Studio project."**
2. Navigate to the cloned repository and select the project folder.

### Sync Project

- Click on **File > Sync Project with Gradle Files** to ensure all dependencies are correctly set up.

### Run the App

1. Connect an Android device or launch an emulator.
2. Click on **Run > Run 'app'** or press **Shift + F10** to build and run the app on the device/emulator.

### Grant Permissions

- Upon launching, the app will request permissions based on the device's Android version. Grant the necessary permissions to allow image access and face detection.

### Test the App

1. Once permissions are granted, the app loads images, detects faces, and displays them in a RecyclerView.
2. Click on a detected face to open a dialog, allowing you to add a custom tag to each face. The tag will then appear above the face’s bounding box.