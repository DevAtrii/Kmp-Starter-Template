# ImageBitmap Utils

File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/utils/image_bitmap/ImageBitmapUtils.kt`

Utilities for converting `ImageBitmap` to `ByteArray`/Base64 and performing compression across Android and iOS.

## Formats

`ImageBitmapCompressFormat` supports:
- Common: `COMMON_WEBP`, `COMMON_JPEG`, `COMMON_PNG`
- Android: `ANDROID_WEBP_LOSSLESS`, `ANDROID_WEBP_LOSSY`
- iOS: `IOS_BMP`, `IOS_GIF`, `IOS_ICO`, `IOS_WBMP`, `IOS_PKM`, `IOS_KTX`, `IOS_ASTC`, `IOS_DNG`, `IOS_HEIF`

Use common formats for cross-platform results; use platform-specific for optimized output on a target OS.

## API

- `expect fun ImageBitmap.toByteArray(commonFormat, quality=100, androidFormat=null, iosFormat=null): ByteArray?`
  - Converts an image to bytes using compression. Returns null if conversion fails.

- `suspend fun ImageBitmap.compress(commonFormat, quality=50, androidFormat=null, iosFormat=null): ImageBitmap?`
  - Produces a new `ImageBitmap` after compressing the original.

- `suspend fun ImageBitmap.Companion.compressByteArray(bytes, commonFormat, quality=50, androidFormat=null, iosFormat=null): ByteArray?`
  - Compresses an existing byte array by decoding to `ImageBitmap` first.

- `suspend fun ImageBitmap.Companion.compressByteArray(bytes, commonFormat, targetMaxKb=100, minQuality=10, step=5, androidFormat=null, iosFormat=null): ByteArray?`
  - Iteratively reduces quality until the byte array size is ≤ target or `minQuality` is reached.

- `suspend fun ImageBitmap.toBase64String(commonFormat, quality=100, androidFormat=null, iosFormat=null): String?`
  - Encodes to Base64 after compression. Uses `ByteArray.toBase64()`.

- `expect suspend fun ImageBitmap.Companion.fromByteArray(bytes: ByteArray): ImageBitmap?`
  - Decodes bytes to `ImageBitmap`.

- `expect suspend fun ImageBitmap.Companion.fromBase64String(base64String: String): ImageBitmap?`
  - Decodes from Base64 (supported data-URI prefixes can be cleaned via `ByteString.cleanBase64Web`).

## Examples

Compress to JPEG 80 and get bytes:
```kotlin
val bytes = imageBitmap.toByteArray(
    commonFormat = ImageBitmapCompressFormat.COMMON_JPEG,
    quality = 80
)
```

Shrink to <= 200 KB (best-effort):
```kotlin
val smaller = ImageBitmap.compressByteArray(
    bytes = original,
    commonFormat = ImageBitmapCompressFormat.COMMON_WEBP,
    targetMaxKb = 200,
    minQuality = 30,
    step = 5
)
```

Base64 result for upload:
```kotlin
val base64 = imageBitmap.toBase64String(
    commonFormat = ImageBitmapCompressFormat.COMMON_WEBP,
    quality = 90
)
```

## Errors
- May throw `EnumException` for platform-specific failures via `fromBase64String`.
- Returns null when conversion fails.
