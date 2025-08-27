# Common Utils (Exception, ByteString, ByteArray)

## EnumException
File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/utils/exception/EnumException.kt`

Lightweight exception carrying an enum reason.

```kotlin
throw EnumException(Base64Error.BYTES_SMALL)
```

- `errorReason`: returns the enum value for inspection.

---

## ByteString Utils
File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/utils/byte_string/ByteStringUtils.kt`

- `ByteString.cleanBase64Web(input: String): String`
  - Strips common `data:image/*;base64,` prefixes
  - Removes spaces and line breaks

Example:
```kotlin
val raw = "data:image/png;base64,iVBORw0..."
val clean = ByteString.cleanBase64Web(raw)
```

---

## ByteArray Utils
File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/utils/byte_array/ByteArrayUtils.kt`

- `ByteArray.toBase64(): String`
  - Encodes bytes using `kotlin.io.encoding.Base64`

Example:
```kotlin
val encoded = pngBytes.toBase64()
```
