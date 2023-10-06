package com.platform.sdk.app.domain.useCase

import com.platform.sdk.Completed
import com.platform.sdk.app.domain.PigmentRepository
import com.platform.sdk.app.domain.entity.Pigment
import com.platform.sdk.failure
import com.platform.sdk.fold
import com.platform.sdk.mvvm.FlowUseCase
import com.platform.sdk.success
import javax.inject.Inject

class FetchPigmentsUseCase @Inject constructor(
    private val repository: PigmentRepository
) : FlowUseCase<List<Pigment>>() {

    override suspend fun executeInternal(): Completed<List<Pigment>> =
        repository.fetchPigments().fold(
            onSuccess = {
                success(it)
            },
            onFailure = {
                failure(it)
            }
        )
}
