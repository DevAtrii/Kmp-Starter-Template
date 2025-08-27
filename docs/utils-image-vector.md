# ImageVector Utils

File: `composeApp/src/commonMain/kotlin/com/kmpstarter/core/utils/image_vector/ImageVectorUtils.kt`

Helpers for converting `ImageVector` to `VectorPainter`, `ImageBitmap`, `ByteArray`, and Base64.

## API

- `@Composable fun ImageVector.asVectorPainter(): VectorPainter`
  - Cached painter for efficient rendering.

- `fun VectorPainter.toImageBitmap(density=Density(1f), layoutDirection=Ltr, size=intrinsicSize, config=Argb8888): ImageBitmap`
  - Renders the vector to a bitmap.

- `@Composable fun ImageVector.toImageBitmap(density=Density(1f), layoutDirection=Ltr, size: Size? = null, config=Argb8888): ImageBitmap`
  - One-step conversion from vector to bitmap.

- `@Composable fun ImageVector.toByteArray(commonFormat, quality=100, density=Density(1f), layoutDirection=Ltr, size: Size? = null, config=Argb8888, androidFormat=null, iosFormat=null): ByteArray?`
  - Converts vector to bitmap, then compresses to bytes.

- `@Composable fun ImageVector.toBase64String(commonFormat, quality=100, density=Density(1f), layoutDirection=Ltr, size: Size? = null, config=Argb8888, androidFormat=null, iosFormat=null): String?`
  - Same as above, but returns Base64.

## Examples

```kotlin
val painter = Icons.Default.Star.asVectorPainter()
Icon(painter = painter, contentDescription = null)
```

```kotlin
val bytes = Icons.Default.Star.toByteArray(
    commonFormat = ImageBitmapCompressFormat.COMMON_PNG,
    quality = 100,
    size = Size(128f, 128f)
)
```

```kotlin
val base64 = Icons.Default.Star.toBase64String(
    commonFormat = ImageBitmapCompressFormat.COMMON_WEBP,
    quality = 90,
    size = Size(64f, 64f)
)
```
