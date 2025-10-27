# Location-Based Memo Reminder App ![App Icon](./app/src/main/assets/app_icon.webp)

This repository is my solution to the Android coding challenge for location-based reminders.

The goal: let the user create a memo with a location, and automatically show a notification when the user physically arrives at that place — even if the app is in the background.

---

## Features

- Create memos with:
  - Title
  - Description
  - Location selected on a map
- Store memos locally using Room
- Automatically trigger a notification when the user enters a 200m radius around the saved location
- Notification shows:
  - Memo title
  - First 140 characters of the memo
  - Small status bar icon
- Works in background (via `BroadcastReceiver`)
- Filter memos on the home screen:
  - All memos
  - Only “open” (not done yet)
- Mark memo as done from the list

---

## High-Level Flow

1. **Create memo**
- Screen: `CreateMemoFragment`
- User enters title & description and picks a location on the map.
- `CreateMemoViewModel` validates input and calls `SaveMemoUseCase`.

2. **Persist + register geofence**
- `SaveMemoUseCase` stores the memo in Room through `MemoRepository`.
- After saving, a geofence is registered via `GeofenceHelper` with:
  - radius = 200m
  - requestId = memo ID
  - trigger = geofence enter

3. **User arrives at the location**
- Android fires `GeofenceBroadcastReceiver`.
- Receiver looks up the memo by ID using `GetMemoByIdUseCase`.
- `NotificationHelper` shows a high-priority notification with the memo content.

4. **User taps around the app**
- `HomeFragment` shows all/open memos in a RecyclerView (`MemoAdapter`).
- `ViewMemoFragment` displays memo details and (if available) a static map pin.

---

## Architecture

The project is structured in 3 layers:

### Presentation layer (`presentation/`)
- Fragments (`HomeFragment`, `CreateMemoFragment`, `ViewMemoFragment`)
- ViewModels (`HomeViewModel`, `CreateMemoViewModel`, `ViewMemoViewModel`)
- UI state is exposed via `StateFlow`
- Navigation handled with Android Navigation Component
- Permissions and user actions are handled in the Fragment, but the logic to decide “what to do next” lives in the ViewModel

### Domain layer (`domain/`)
- `Memo` data model
- Use cases:
  - `SaveMemoUseCase`
  - `GetAllMemosUseCase`
  - `GetOpenMemosUseCase`
  - `GetMemoByIdUseCase`
  - `UpdateMemoDoneStatusUseCase`
  - `ValidateMemoUseCase`
  - `AddGeofenceForMemoUseCase`
- `MemoRepository` interface (abstraction over data layer)

This layer contains business rules and is UI-agnostic.

### Data layer (`data/`)
- Room:
  - `MemoModel`, `MemoDao`, `AppDatabase`
- Mapping between Room entities and domain (`MemoMapperImpl`)
- `MemoRepositoryImpl`:
  - reads/writes memos
  - registers/removes geofences
  - returns `Result<T>` for error safety
- `GeofenceHelper`:
  - wraps `GeofencingClient`
  - creates/removes geofences
- `GeofenceBroadcastReceiver`:
  - triggered in background
  - loads memo
  - posts notification via `NotificationHelper`

`NotificationHelper` also creates the notification channel on app startup.

---

## Permissions

At runtime the app requests:

- **Fine location** (`ACCESS_FINE_LOCATION`)  
  Needed to pick a point on the map and get current location.

- **Background location** (`ACCESS_BACKGROUND_LOCATION`, on Android 10+)  
  Needed so the geofence can still trigger while the app is not in the foreground.

- **Notifications** (`POST_NOTIFICATIONS`, on Android 13+)  
  Needed to actually show the reminder.

`CreateMemoFragment` coordinates these permission flows.  
If permissions are missing, the user sees rationale dialogs or a Snackbar, and in the “permanently denied” case is guided to system settings.

---

## Building & Running

- The project is written in Kotlin.
- The codebase was tested with **Android Studio Narwhal Feature Drop**.
- Please **disable the NDK plugin** if it’s enabled in your environment; the project doesn’t use NDK, and it can cause sync noise.
- Steps:
  1. Open the project in Android Studio.
  2. Let Gradle sync.
  3. Run on a device or emulator with Google Play Services.
  4. Grant all requested permissions when prompted.

No additional setup, API keys, or secrets are required.

---

## Testing the Geofence Behavior

To simulate the notification:

1. Create a memo with a chosen location.
2. Ensure background location + notification permissions are granted.
3. Move the device (or emulator GPS) to within ~200 meters of that location.
4. You should get a notification with:
- the memo title, and
- the first 140 chars of the memo text.

On an emulator you can do this using the “Location” controls in the Extended Controls panel.

---

Thanks for reviewing the solution 🙌  
This version is intended to be close to something I'd be comfortable shipping in a real app (permission flows, background behavior, clean layering), not just a quick prototype.
