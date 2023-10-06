package com.platform.sdk.app.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.platform.sdk.AsyncOperation
import com.platform.sdk.Completed
import com.platform.sdk.Loading
import com.platform.sdk.NetworkConnectionFailure
import com.platform.sdk.app.domain.entity.Pigment
import com.platform.sdk.app.domain.useCase.FetchPigmentsUseCase
import com.platform.sdk.failure
import com.platform.sdk.loading
import com.platform.sdk.onFailure
import com.platform.sdk.onSuccess
import com.platform.sdk.success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val fetchPigmentsUseCase: FetchPigmentsUseCase
) : ViewModel() {

    private val _pigments by lazy { MutableStateFlow<AsyncOperation<List<Pigment>>>(loading()) }
    val pigments: StateFlow<AsyncOperation<List<Pigment>>> get() = _pigments

    init {
        fetchPigments()
    }

    private fun fetchPigments() = viewModelScope.launch {
        fetchPigmentsUseCase.execute()
            .collect { operation ->
                if (operation is Completed) {
                    operation.result
                        .onSuccess {
                            delay(10_000)
                            _pigments.value = success(it)
                        }
                        .onFailure {
                            _pigments.value = failure(
                                NetworkConnectionFailure("No network connection")
                            )
                        }
                }
            }
    }
}
