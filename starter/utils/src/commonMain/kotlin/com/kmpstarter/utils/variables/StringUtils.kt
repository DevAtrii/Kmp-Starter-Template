package com.kmpstarter.utils.variables

inline fun String.onEmpty(action: () -> Unit) : String {
    return this.also {
        if (it.isEmpty()) action()
    }
}
inline fun String.onNotEmpty(action: () -> Unit) : String {
    return this.also {
        if (!it.isEmpty()) action()
    }
}



inline fun <T> String.takeIfEmpty(action: () -> T): T? =
    if (this.isEmpty()) action() else null

inline fun <T> String.takeIfNotEmpty(action: () -> T): T? =
    if (this.isEmpty()) null else action()


fun String.isEmail(): Boolean =
    if (isEmpty()) {
        false
    } else {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        this.matches(emailPattern.toRegex())
    }