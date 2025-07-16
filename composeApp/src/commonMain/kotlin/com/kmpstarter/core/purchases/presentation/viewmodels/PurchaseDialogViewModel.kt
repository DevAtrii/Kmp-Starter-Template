package com.kmpstarter.core.purchases.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.kmpstarter.core.datastore.common.CommonDataStore
import com.kmpstarter.core.utils.common.hoursToMillis
import com.kmpstarter.core.utils.logging.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class PurchaseDialogViewModel(
    private val commonDataStore: CommonDataStore,
) : ViewModel() {
    private val _expirationMillis = MutableStateFlow(0L)
    val expirationMillis = _expirationMillis.asStateFlow()
    private val _showDiscountDialog = MutableStateFlow(false)
    val showDiscountDialog = _showDiscountDialog.asStateFlow()

    companion object {
        private const val TAG = "PurchaseDialogViewModel"
        private const val DEFAULT_EXPIRATION_HOUR = 1
    }

    private var getDiscountStatusJob: Job? = null

    init {
        getDiscountExpirationStatus()
    }

    private fun getDiscountExpirationStatus() {
        getDiscountStatusJob?.cancel()
        getDiscountStatusJob = CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Getting Discount Expiration Status")
            val currentMillis = Clock.System.now().toEpochMilliseconds()
            commonDataStore.installMillis.map { millis ->
                (millis) + hoursToMillis(hour = DEFAULT_EXPIRATION_HOUR)
            }.collect { millis ->
                val showDiscount = millis > currentMillis
                Log.d(
                    TAG,
                    "Exp Millis: $millis, current: $currentMillis, bool: $showDiscount"
                )
                _expirationMillis.update {
                    millis
                }
                _showDiscountDialog.update {
                    showDiscount
                }
            }

        }
    }

}



















