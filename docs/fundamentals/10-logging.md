# Logging

Starter Template provides a simple logging utility via the `Log` object.
You can use it anywhere in your Kotlin code to log messages consistently.

---

## Usage

Use the following functions:

```kotlin linenums="1"
Log.d(TAG, "Debug message")
Log.i(TAG, "Info message")
Log.w(TAG, "Warning message")
Log.e(TAG, "Error message")
```

You can also pass multiple items:

```kotlin linenums="1"
Log.d(TAG, "User ID:", userId, "Action:", actionName)
```
!!! tip "Default Tag" 
    If you want the **default tag**, pass `null`:

    ```kotlin linenums="1"
    Log.d(null, "Debug message with default tag")
    ```


---

## IDE Live Templates (Recommended)

Create live templates in your IDE for faster logging.

Scope: **Kotlin**

| Abbreviation | Template                                                          | Notes                                |
| ------------ | ----------------------------------------------------------------- | ------------------------------------ |
| `klogd`      | `com.kmpstarter.utils.logging.Log.d(null,"$FUNNAME$: $MESSAGE$")` | `$FUNNAME$` → `kotlinFunctionName()` |
| `klogi`      | `com.kmpstarter.utils.logging.Log.i(null,"$FUNNAME$: $MESSAGE$")` | `$FUNNAME$` → `kotlinFunctionName()` |
| `klogw`      | `com.kmpstarter.utils.logging.Log.w(null,"$FUNNAME$: $MESSAGE$")` | `$FUNNAME$` → `kotlinFunctionName()` |
| `kloge`      | `com.kmpstarter.utils.logging.Log.e(null,"$FUNNAME$: $MESSAGE$")` | `$FUNNAME$` → `kotlinFunctionName()` |

!!! note "Live Templates"
    Using live templates will **save tons of time** when writing log statements.
    Official JetBrains documentation: <a href="https://www.jetbrains.com/help/idea/using-live-templates.html" target="_blank">Live Templates</a>
