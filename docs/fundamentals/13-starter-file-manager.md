---
comments: false
icon: lucide/folder-open
---

# Starter File Manager

`StarterFileManager` is the starter's cross-platform file API for common save / list / read / rename / delete / share / open flows.

It lives in `starter:utils` and returns `Result` for every operation so you can handle success and failure without platform-specific try/catch soup.

For advanced file IO (pickers, multi-select, richer sharing), use
[FileKit](https://filekit.mintlify.app/introduction).

!!! warning "Experimental API"
    `StarterFileManager` is marked `@ExperimentalStarterApi`. Opt in at the call site (or file / module) with `@OptIn(ExperimentalStarterApi::class)`.

---

## Getting an Instance

### Compose (recommended)

Use `rememberStarterFileManager()` from `starter:ui:utils`:

```kotlin title="Compose" linenums="1"
@OptIn(ExperimentalStarterApi::class)
@Composable
fun ExportScreen() {
    val fileManager = rememberStarterFileManager()

    Button(
        onClick = {
            // launch coroutine / viewModel call
        }
    ) {
        Text("Export")
    }
}
```

On Android this binds the current `ComponentActivity`. That is required for:

- `saveFileIn` (system Save As picker)
- `shareFile` (share sheet)
- `openFile` (open with another app)

!!! tip "Pass it into a ViewModel"
    You can hand this Activity-bound instance to a ViewModel with **Koin parameters** so UI actions like Save As / Share / Open still work:

    ```kotlin title="FeatureModule.kt" linenums="1"
    val featurePresentationModule = module {
        viewModel { (fileManager: StarterFileManager) ->
            ExportViewModel(fileManager = fileManager)
        }
    }
    ```

    ```kotlin title="ExportViewModel.kt" linenums="1"
    class ExportViewModel(
        private val fileManager: StarterFileManager,
    ) : ViewModel() {
        fun share(path: String) {
            viewModelScope.launch {
                fileManager.shareFile(path = path)
            }
        }
    }
    ```

    ```kotlin title="ExportScreen.kt" linenums="1"
    @Composable
    fun ExportScreen() {
        val fileManager = rememberStarterFileManager()
        val viewModel: ExportViewModel = koinViewModel {
            parametersOf(fileManager)
        }
        // ...
    }
    ```

### Koin (non-UI / background)

`StarterFileManager` is also registered in DI. The Android singleton is created with `activity = null`.

Use Koin for Downloads / cache ops that do **not** need an Activity — typically from a **repository**:

```kotlin title="ReportRepository" linenums="1"
class ReportRepository(
    private val fileManager: StarterFileManager,
) {
    suspend fun cacheReport(bytes: ByteArray): Result<StarterFile> {
        return fileManager.saveInCache(
            file = "report",
            folderPath = "exports",
            extension = "pdf",
            content = bytes,
            mimeType = "application/pdf",
        )
    }

    suspend fun listCachedReports(): Result<List<StarterFile>> {
        return fileManager.getFilesFromCache(path = "exports")
    }
}
```

```kotlin title="FeatureDataModule.kt" linenums="1"
val featureDataModule = module {
    singleOf(::ReportRepository)
}
```

!!! danger "Do not use Koin for saveFileIn / shareFile / openFile on Android"
    Those methods fail if no Activity is available. Message points you to `rememberStarterFileManager()` (then pass it into the ViewModel with `parametersOf` if needed).

---

## Core Types

| Type | Meaning |
| :--- | :--- |
| `FileName` | Name **without** extension (`report`) |
| `FileExtension` | Extension **without** leading dot (`pdf`) |
| `FolderPath` | Relative folder (`exports` / `MyApp/docs`) |
| `FilePath` / `Path` | Platform path or content URI string |
| `FileMimeType` | MIME type (`application/pdf`) |
| `FileContent` | `ByteArray` |
| `StarterFile` | Metadata: `path`, `name`, `extension`, `mimeType`, `sizeBytes`, timestamps |

Always pass **name and extension separately** when writing. When reading / deleting / renaming / sharing Downloads files, pass `StarterFile.path` from a list call.

---

## Downloads

### Save

```kotlin title="Save to Downloads" linenums="1"
fileManager.saveFileIntoDownloads(
    file = "invoice",
    folderPath = "MyApp",
    extension = "pdf",
    content = pdfBytes,
    mimeType = "application/pdf",
)
```

!!! note "Platform behavior"
    - **Android:** public Downloads (MediaStore on API 29+, legacy file path below).
    - **iOS:** app Documents directory (`Documents/...`). iOS does not allow writing the user's public Downloads folder.

### List / get / read / rename / delete

```kotlin title="Downloads workflow" linenums="1"
val files = fileManager.getFilesFromDownloads(path = "MyApp").getOrElse { emptyList() }

val target = fileManager.getFileFromDownloads(file = "invoice", path = "MyApp").getOrThrow()
// or: files.firstOrNull { it.name == "invoice" && it.extension == "pdf" }

fileManager.readFromDownloads(path = target.path)
fileManager.renameFromDownloads(path = target.path, to = "invoice_final")
fileManager.deleteFromDownloads(path = target.path)
```

`renameFrom*` keeps the existing extension — `to` is the new **name only**.
`saveFileIntoDownloads` / `renameFromDownloads` return the resulting `StarterFile`.

---

## Cache

Cache paths are **relative** to the app cache directory.

```kotlin title="Cache workflow" linenums="1"
val saved = fileManager.saveInCache(
    file = "draft",
    folderPath = "temp",
    extension = "txt",
    content = text.encodeToByteArray(),
    mimeType = "text/plain",
).getOrThrow()

fileManager.getFilesFromCache(path = "temp")
fileManager.getFileFromCache(file = "draft", path = "temp")
fileManager.readFromCache(path = "temp/draft.txt")
fileManager.renameFromCache(path = "temp/draft.txt", to = "draft_v2")
fileManager.deleteFromCache(path = "temp/draft_v2.txt")
```

Use cache for temporary / app-private data. Prefer Downloads or `saveFileIn` when the user should keep the file outside your app.

---

## Save With System Picker

`saveFileIn` opens the platform Save As / export UI:

```kotlin title="Save As" linenums="1"
@Composable
fun SaveAsButton(bytes: ByteArray) {
    val fileManager = rememberStarterFileManager()
    val scope = rememberCoroutineScope()

    Button(
        onClick = {
            scope.launch {
                fileManager.saveFileIn(
                    suggestedName = "export",
                    extension = "csv",
                    content = bytes,
                    mimeType = "text/csv",
                )
            }
        }
    ) {
        Text("Save As…")
    }
}
```

---

## Share

```kotlin title="Share a file" linenums="1"
fileManager.shareFile(path = target.path)          // Downloads StarterFile.path
fileManager.shareFile(path = "temp/draft.txt")     // cache-relative
```

## Open With System App

`openFile` asks the system which app should open the file (Android `ACTION_VIEW` chooser / iOS Open In menu):

```kotlin title="Open a file" linenums="1"
fileManager.openFile(path = target.path)           // Downloads StarterFile.path
fileManager.openFile(path = "temp/draft.txt")      // cache-relative
```

Use `rememberStarterFileManager()` on Android (same Activity requirement as Share).

### Android FileProvider (host app)

Sharing or opening **local / cache file paths** needs a `FileProvider` in the **host app** (`androidApp`). The utils module does **not** merge one — you decide whether to include it.

`androidApp` already ships a working setup:

1. Provider in `AndroidManifest.xml` with authority `${applicationId}.starter.fileprovider`
2. Paths XML at `res/xml/starter_file_paths.xml`

```xml title="androidApp/.../AndroidManifest.xml"
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.starter.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/starter_file_paths" />
</provider>
```

```xml title="androidApp/.../res/xml/starter_file_paths.xml"
<paths>
    <cache-path name="cache" path="." />
    <files-path name="files" path="." />
    <external-path name="external" path="." />
</paths>
```

!!! tip "content:// URIs"
    MediaStore Downloads URIs (`content://…`) do **not** need FileProvider. FileProvider is for filesystem / cache paths used by `shareFile` and `openFile`.

---

## Handling Results

Every method returns `Result`:

```kotlin linenums="1"
fileManager.saveInCache(...).fold(
    onSuccess = { /* done */ },
    onFailure = { error -> Log.e(TAG, error.message) },
)
```

`readFromDownloads` / `readFromCache` return `Result<Pair<StarterFile, FileContent>>` — metadata + bytes together.

---

## Migrating from KmpFileManager

`KmpFileManager` is deprecated. Prefer `StarterFileManager`:

```kotlin title="Before"
kmpFileManager.saveFileToDownloadsFolder(
    fileName = "report.pdf",
    folderName = "MyApp",
    fileContent = bytes,
    mimeType = "application/pdf",
)
```

```kotlin title="After"
starterFileManager.saveFileIntoDownloads(
    file = "report",
    folderPath = "MyApp",
    extension = "pdf",
    content = bytes,
    mimeType = "application/pdf",
)
```

---

### Summary

* Prefer `rememberStarterFileManager()` in Compose; pass it into a ViewModel with `parametersOf` when Save As / Share / Open need Activity.
* Use the Koin singleton from repositories for Downloads / cache ops that do not need an Activity.
* Downloads on Android → public Downloads; on iOS → Documents.
* Cache paths are relative; Downloads paths come from `StarterFile.path`.
* Host app owns FileProvider for `shareFile` / `openFile` with local paths.
* Reach for FileKit when you need advanced file IO beyond this API.
