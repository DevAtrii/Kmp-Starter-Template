package com.kmpstarter.utils.data_classes

import com.kmpstarter.utils.variables.ifTrue
import kotlinx.serialization.Serializable

/**
 * Creates a ComparableState where the initial value and the current value are both set to this
 * object.
 */
fun <T> T.asComparableState(): ComparableState<T> {
    return ComparableState(value = this, initialValue = this)
}

@Serializable
data class ComparableState<T>(
    val value: T,
    val initialValue: T,
) {

    companion object {
        private const val TAG = "ComparableState"
    }

    /**
     * True if the current value is different from the initial value, indicating that changes have
     * been made.
     */
    val hasChanges: Boolean
        get() = value != initialValue

    /**
     * Creates a new ComparableState with the new value, keeping the original initialValue. This is
     * useful when the user is editing.
     */
    fun updateValue(newValue: T): ComparableState<T> {
        return ComparableState(value = newValue, initialValue = this.initialValue)
    }

    /**
     * Creates a new ComparableState where the current value becomes the new initial value (e.g.,
     * after a successful save operation).
     */
    fun reset(): ComparableState<T> {
        return ComparableState(value = this.value, initialValue = this.value)
    }

    /**
     * Creates a new ComparableState where the current value becomes newValue new initial value
     * (e.g., after a successful save operation).
     */
    fun updateAndReset(newValue: T): ComparableState<T> {
        return ComparableState(value = newValue, initialValue = newValue)
    }

    /**
     * updates initialValue & value if not error or isSaving=true if error then updates the
     * currentValue only, leaves the initialValue this will keep the state sync from shared view
     * models & current view model, ensuring if error then don't update the current state this will
     * allow user to save changes again else if not error & saving then use the current value & if
     * not error & not saving then updates the new value which comes from sharedViewModel
     */
    fun updateAndResetIfNotError(
        isError: Boolean,
        isSaving: Boolean,
        newValue: T,
    ): ComparableState<T> {
        val _newValue = (isSaving || isError).ifTrue(this.value) ?: newValue

        val value = if (isError || isSaving) updateValue(_newValue) else updateAndReset(_newValue)


        return value
    }
}
