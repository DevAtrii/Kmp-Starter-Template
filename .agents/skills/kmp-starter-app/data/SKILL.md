---
name: kmp-starter-data
description: Data layer on the KMP Starter Template — repositories and data sources, Logics guidance, DataStore persistence, Room database, StarterFileManager, and the Calf file picker.
---

# Data Layer

## Repositories & data sources

- Domain defines the interface; data implements it. See the onboarding trio:
  - `features/core/domain/.../repositories/OnboardingRepository.kt`
  - `features/core/data/.../repositories/OnboardingRepositoryImpl.kt`
- Bind impl to interface via Koin (`singleOf(::Impl) bind Interface::class`).

## Logics (use cases) — only when meaningful

Logics are use cases. Create them **only** for meaningful user-driven actions.

Good: `RegisterAccountLogic`, `LogoutLogic`, `PurchaseSubscriptionLogic`, `DeleteAccountLogic`, `ExportNotesLogic`, `BackupDatabaseLogic`.

Bad: `ValidateEmailLogic`, `FormatDateLogic`, `GetUsersLogic`, `ParseJsonLogic`, `SavePreferenceLogic`.

Simple operations belong in repositories, extension functions, or utilities. Do not wrap every repository call in a Logic. A `Logic` is a class with `suspend operator fun invoke()`; aggregate multiple Logics into a `data class XLogics(...)` if the ViewModel needs several.

## Persistence — DataStore

Use Starter DataStores; do not introduce another preference framework.

Non-Compose (ViewModels/repos) — inject `AppDataStore` and use delegates:

```kotlin
class AuthViewModel(appDataStore: AppDataStore) : ViewModel() {
    private val accessToken = appDataStore.stringDataStore("access_token")
    private val isLoggedIn = appDataStore.booleanDataStore("is_logged_in", default = false)
    private val settings = appDataStore.serializableDataStore("settings", default = Settings())
    // API: flow, get(), set(value), clear()
}
```

Factories: `stringDataStore`, `intDataStore`, `longDataStore`, `booleanDataStore`, `floatDataStore`, `doubleDataStore`, `stringSetDataStore`, `byteArrayDataStore`, `serializableDataStore`.

Compose — `remember*DataStore` helpers:

```kotlin
val themeMode by rememberStringDataStore("theme_mode", "LIGHT")
var counter by rememberMutableIntDataStore("counter", 0)
```

## Database (Room)

If `features/database` exists, define entities, DAOs, and migrations there; other features consume it.

- Entity: `@Entity` data class with `@PrimaryKey`.
- DAO: `@Dao` interface with `@Query` / `@Insert` / `@Update` / `@Delete`.
- Register entity + DAO in `KmpStarterDatabase` (`@Database(entities=[...], version=DB_VERSION)`).
- Bump `DB_VERSION` on schema change; add migrations to `KmpStarterDatabaseMigrations.SUPPORTED_MIGRATIONS`.

If the project was generated without the database feature, ask the user to regenerate with Database enabled.

## StarterFileManager

Cross-platform file API in `starter:utils`; returns `Result` for every operation. Marked `@ExperimentalStarterApi` — opt in with `@OptIn(ExperimentalStarterApi::class)`.

```kotlin
// Compose (Activity-bound; required for saveFileIn / shareFile / openFile)
val fileManager = rememberStarterFileManager()

// Repos / background (no Activity needed)
class ReportRepository(private val fileManager: StarterFileManager) {
    suspend fun cacheReport(bytes: ByteArray) = fileManager.saveInCache(
        file = "report", folderPath = "exports", extension = "pdf",
        content = bytes, mimeType = "application/pdf",
    )
}
```

Key types: pass `file` (name, no extension) and `extension` (no dot) separately. `StarterFile` holds `path`, `name`, `extension`, `mimeType`, `sizeBytes`, timestamps.

- Downloads: `saveFileIntoDownloads`, `getFilesFromDownloads`, `getFileFromDownloads`, `readFromDownloads`, `renameFromDownloads`, `deleteFromDownloads` (Android → public Downloads; iOS → Documents).
- Cache: `saveInCache`, `getFilesFromCache`, `getFileFromCache`, `readFromCache`, `renameFromCache`, `deleteFromCache` (paths relative to cache dir).
- System picker: `saveFileIn(suggestedName, extension, content, mimeType)`.
- Share/open: `shareFile(path)`, `openFile(path)` — need Activity on Android (use `rememberStarterFileManager()`).
- Android host app owns `FileProvider` for local paths (`androidApp/.../AndroidManifest.xml` + `res/xml/starter_file_paths.xml`).

Pass an Activity-bound instance into a ViewModel with Koin parameters (`parametersOf(fileManager)`).

See `https://starter.atherio.dev/fundamentals/13-starter-file-manager/` for full API.

## File picker

Use the bundled Calf library for file picking. Do not introduce another picker library.
