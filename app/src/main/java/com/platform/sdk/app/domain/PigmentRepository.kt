package com.platform.sdk.app.domain

import com.platform.sdk.Failure
import com.platform.sdk.Result
import com.platform.sdk.app.domain.entity.Pigment

interface PigmentRepository {

    suspend fun fetchPigments(): Result<List<Pigment>, Failure>
}