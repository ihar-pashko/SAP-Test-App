# Android Memos App 📝

This Android application allows users to create and manage memos, with a special feature for location-based reminders. The project is built entirely in Kotlin, following modern Android development practices.

**(!) Important:** If you have the NDK plugin installed, please disable it for this project in Android Studio (`File` -> `Project Structure` -> `SDK Location` -> Untick `Download Android NDK if not installed`), as errors may occur.

---

## Key Feature: Location-Based Notifications 📍🔔

The app includes a powerful feature for setting reminders based on geographic location:

1.  **Add Location to Memo:** When creating a new memo, you can optionally select a specific location on an integrated Google Map.
2.  **Automatic Reminders:** Once a memo with a location is saved, the app uses efficient background monitoring. When your device enters a 200-meter radius around the saved location, you'll receive a notification.
3.  **Informative Notifications:** The notification conveniently displays the memo's title and the beginning of its description (up to 140 characters), along with a custom app icon.
4.  **Works Anytime:** The location monitoring and notification triggering function reliably, even if the app is running in the background or has been closed.

---

## 🛠️ Tech Stack & Architecture

This project utilizes a modern tech stack and architectural patterns:

* **Language:** **Kotlin** (100%)
* **Architecture:**
    * **Clean Architecture:** Divided into **Data**, **Domain**, and **Presentation** layers.
    * **MVVM** (Model-View-ViewModel): Separating UI logic from business logic.
    * **Single-Activity Architecture:** Using the **Android Navigation Component** to manage Fragments within a single `MainActivity`.
* **UI:**
    * **Android Fragments:** Modular UI components.
    * **ViewBinding:** Safe view access.
    * **Material Design Components:** Standard Material UI elements (`MaterialToolbar`, `MaterialButton`, `TextInputLayout`, etc.).
    * **RecyclerView with `ListAdapter` & `DiffUtil`:** Efficient list display.
    * **Google Maps SDK:** Map display and location selection.
* **Asynchronous Programming:**
    * **Kotlin Coroutines:** Background thread management.
    * **Kotlin Flow (`StateFlow`, `Channel`):** Reactive data streams and event handling. Offline-first approach for observing database changes.
* **Dependency Injection:**
    * **Koin:** Managing dependencies across layers.
* **Data Persistence:**
    * **Room Persistence Library:** Local SQLite database storage with migrations.
    * **Indices:** Database index on `isDone` column for query optimization.
* **Location & Background:**
    * **Google Play Services - Location:** **Geofencing API** for efficient background location monitoring.
    * **BroadcastReceiver:** Receiving geofence events when the app is inactive.
* **Notifications:**
    * **`NotificationManagerCompat` & Notification Channels:** Creating system notifications.
* **Code Quality:**
    * **Detekt:** Static code analysis with formatting rules via `detekt.yml`.
* **Build System:**
    * **Gradle:** Using **Version Catalogs** (`libs.versions.toml`) for dependency management.