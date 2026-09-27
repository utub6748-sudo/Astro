# Astro — Android finance & life planner

Astro is a local-first Android app for personal finance, habits, goals and tasks.

## Included

- Finance dashboard with automatic totals in a selected base currency.
- Income and expense transactions in any currency.
- Per-transaction exchange rate to the base currency.
- Custom spending/income groups.
- Habit tracker with daily completion state.
- Financial goals with progress tracking.
- Tasks with exact date/time and optional end time.
- Light / dark / system theme.
- Static accent or gradient accent with editable preset colors.
- JSON export/import backup. Data stays on the device unless the user exports it.
- Astro launcher icon based on the supplied reference image.

## Build

Requirements: Android Studio with JDK 17 and Android SDK 36.

The project is configured for Android Gradle Plugin 8.13.2, Gradle 8.13, Kotlin 2.3.21 and Compose BOM 2026.09.00.

### Android Studio

Open the `Astro` folder and run the `app` configuration.

### Command line

If Gradle 8.13 is installed:

```bash
gradle assembleDebug
```

The APK will be produced at:

`app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions

The repository includes `.github/workflows/build-apk.yml`. It installs Gradle 8.13 in the runner and uploads `app-debug.apk` as a workflow artifact.

## Data ownership

The v1 app does not require an account or cloud service. Data is persisted locally in app-private storage. The JSON backup contains the app data and can be copied by the user.
