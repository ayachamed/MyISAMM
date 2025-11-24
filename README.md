# My ISAMM - Android Education App

## Project Overview

My ISAMM is an Android application developed using Java Native for managing and accessing educational materials, similar in concept to a simplified Moodle. It allows users to browse course materials organized hierarchically by department, program, year, semester, course, and section. Initially, all authenticated users can also upload materials. The application heavily utilizes Firebase for backend services including authentication, database, and file storage.

This project was developed as part of an educational requirement, focusing on core Android development principles and Firebase integration.

## Features

*   **User Authentication:**
    *   Email/Password based Sign Up and Login.
    *   Persistent login state using Firebase Authentication.
*   **Material Browsing:**
    *   Hierarchical navigation through educational structure:
        *   Department (Multimedia, Informatique, Audiovisuel)
        *   Program (e.g., CMM, MIME, Montage)
        *   Year
        *   Semester
        *   Course
        *   Section (e.g., TD, TP, Cours)
    *   Dynamic loading of categories and materials from Firebase.
*   **Technical Features:**
    *   Built with Java Native for Android.
    *   Utilizes multiple Activities/Fragments for navigation and functionality.
    *   Implements various UI Widgets (`Button`, `EditText`, `TextView`, `ImageView`, `RecyclerView`, `CardView`).
    *   Handles data passing between screens (e.g., selected category IDs).
    *   Gracefully handles orientation changes, preserving essential state using appropriate Android lifecycle methods or ViewModels.
    *   Leverages Firebase for backend:
        *   **Firebase Authentication:** User management.
        *   **Firebase Firestore/Realtime Database:** Storing hierarchical structure and file metadata.
        *   **Firebase Storage:** Storing uploaded files.

## Technology Stack

*   **Language:** Java
*   **Platform:** Android Native
*   **IDE:** Android Studio
*   **Backend Services:** Firebase
    *   Authentication
    *   Firestore (or Realtime Database - specify final choice)
    *   Storage


## Screenshots
![MyISAMM](https://github.com/user-attachments/assets/cf0d0f2e-b637-4fa9-beaa-59647ec05bff)

1.  **Login Screen:** Fields for Email, Password, "Log In" button, "Forgot Password" link, and link to Sign Up.
2.  **Sign Up Screen:** Fields for Email, Password, Confirm Password, "Sign up" button. Includes app logo (`MyISAMM`).
3.  **Home/Dashboard Screen:** Welcome message with Username, potentially an image related to ISAMM, navigation options (e.g., "Clubs", main content area). Includes bottom navigation bar (Home, My Courses, My Profile, About).
4.  **Department Selection Screen:** List of departments (Multimedia, Audiovisuel, Informatique) presented likely using CardViews or a similar list format with icons. Includes bottom navigation bar.
5.  **Category/Sub-Category Screen:** Placeholder view showing navigation hierarchy (e.g., "Catégories Précédentes"), indicating further drill-down levels, likely using RecyclerView with CardViews. Includes bottom navigation bar.

## Project Structure Highlights

*   The application utilizes multiple Activities (e.g., `LoginActivity`, `SignUpActivity`, `MainActivity`) and potentially Fragments for different sections like browsing or profile management, fulfilling the requirement of using at least 2 Activities or 2 Fragments.
*   Data is passed between Activities/Fragments using `Intent` extras or `Bundle` arguments.
*   UI Layouts are defined in XML, making use of `ConstraintLayout`, `LinearLayout`, `RecyclerView`, `CardView`, `EditText`, `Button`, `TextView`, `ImageView`.
*   Firebase interactions are encapsulated within relevant Activities/Fragments or potentially dedicated helper classes.

## Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd <repository-directory>
    ```
2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open an Existing Project".
    *   Navigate to and select the cloned repository directory.
3.  **Firebase Setup (Crucial):**
    *   Go to the [Firebase Console](https://console.firebase.google.com/).
    *   Create a new Firebase project.
    *   **Register your Android app:**
        *   Click on the Android icon to add an Android app.
        *   Enter the package name exactly as it is in your `app/build.gradle` file (e.g., `com.example.myisamm_app`).
        *   Provide a nickname and optionally the SHA-1 debug signing certificate fingerprint.
    *   **Download `google-services.json`:** Download the configuration file provided by Firebase.
    *   **Add Config File:** Place the downloaded `google-services.json` file into the `app/` directory of your Android Studio project.
    *   **Enable Firebase Services:**
        *   In the Firebase Console, navigate to "Authentication" -> "Sign-in method" and enable the "Email/Password" provider.
        *   Navigate to "Firestore Database" (or "Realtime Database") and create a database. Start in **test mode** for initial development (allows open read/write) or configure security rules for authenticated users (`allow read, write: if request.auth != null;`).
        *   Navigate to "Storage" and click "Get Started". Choose test mode or configure security rules similar to the database (`allow read, write: if request.auth != null;`).
    *   **Sync Project:** Sync your Android Studio project with Gradle files (`File -> Sync Project with Gradle Files`).
4.  **Build and Run:**
    *   Select a target device (emulator or physical device).
    *   Click the "Run" button (green play icon) in Android Studio.

## Firebase Data Structure (Example - Firestore)

The database should be structured to represent the hierarchy. Example Firestore structure:

*   **Collection:** `departments`
    *   **Document:** `multimedia` (ID: `multimedia`)
        *   `name`: "Départemant Multimédia"
        *   **Subcollection:** `programs`
            *   **Document:** `cmm` (ID: `cmm`)
                *   `name`: "CMM"
                *   **Subcollection:** `years` -> `semesters` -> `courses` -> `sections` -> `materials` (metadata docs)
            *   **Document:** `cc_3d` (ID: `cc_3d`) ...
            *   **Document:** `mdca` (ID: `mdca`) ...
    *   **Document:** `informatique` (ID: `informatique`)
        *   `name`: "Départemant Informatique"
        *   **Subcollection:** `programs`
            *   **Document:** `mime` (ID: `mime`) ...
            *   **Document:** `jv` (ID: `jv`) ...
*   **Collection:** `materials` (Alternative top-level collection for metadata, referencing paths)
    *   **Document:** `<unique_material_id>`
        *   `fileName`: "Lecture 1.pdf"
        *   `storagePath`: "/materials/informatique/mime/year1/sem1/courseA/td/Lecture 1.pdf"
        *   `uploaderId`: "<firebase_user_uid>"
        *   `timestamp`: Timestamp
        *   `departmentId`: "informatique"
        *   `programId`: "mime"
        *   ... other hierarchy levels ...
        *   
## Key Concepts Learned/Applied

*   Firebase Integration (Auth, Firestore/RTDB, Storage)
*   Activity & Fragment Lifecycles
*   Android UI Design with XML (ConstraintLayout, LinearLayout, etc.)
*   Using common Android Widgets (`EditText`, `Button`, `TextView`, `ImageView`)
*   Implementing `RecyclerView` with `ViewHolder` and `Adapter` patterns for dynamic lists.
*   Using `CardView` for modern UI elements.
*   Navigation between Activities/Fragments using Intents/Bundles.
*   Handling Asynchronous operations (Firebase callbacks/listeners).
*   Requesting Runtime Permissions (e.g., for file access/storage).
*   Basic State Management during Orientation Changes (e.g., using `onSaveInstanceState` or `ViewModel`).

## Future Improvements

*   Add search functionality for materials.
*   Implement offline caching/access for downloaded materials.
*   Improve error handling and user feedback.
"# MyISAMM2" 
