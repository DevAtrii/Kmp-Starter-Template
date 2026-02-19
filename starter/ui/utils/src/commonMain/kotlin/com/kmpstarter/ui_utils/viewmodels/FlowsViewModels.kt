package com.kmpstarter.ui_utils.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

context(viewModel: ViewModel)
fun <T> Flow<T>.collectInViewModel(collector: FlowCollector<T>) {
    val flow = this
    viewModel.viewModelScope.launch {
        flow.collect(collector)
    }
}

context(viewModel: ViewModel)
fun <T> Channel<T>.sendInViewModel(value: T) {
    val channel = this
    viewModel.viewModelScope.launch {
        channel.send(value)
    }
}

context(viewModel: ViewModel)
fun <T> MutableSharedFlow<T>.emitInViewModel(value: T) {
    val sharedFlow = this
    viewModel.viewModelScope.launch {
        sharedFlow.emit(value)
    }
}
