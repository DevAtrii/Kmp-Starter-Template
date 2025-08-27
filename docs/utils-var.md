# var Utils

Files:
- `composeApp/src/commonMain/kotlin/com/kmpstarter/core/utils/var/BoolUtils.kt`
- `composeApp/src/commonMain/kotlin/com/kmpstarter/core/utils/var/GenericUtils.kt`

## API

- `infix fun <T> Boolean.ifTrue(value: T): T?`
- `infix fun <T> Boolean.ifFalse(value: T): T?`

Example:
```kotlin
val maybe: String? = isPremium.ifTrue("Premium")
```

- `infix fun <T> T?.elze(value: T): T`

Example:
```kotlin
val name = nullableName elze "Guest"
```
