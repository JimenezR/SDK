package com.platform.sdk.app.data.dataSource

import com.platform.sdk.Failure
import com.platform.sdk.Result
import com.platform.sdk.app.domain.entity.Pigment

internal interface PigmentLocalDataSource {
    suspend fun fetchPigments(): Result<List<Pigment>, Failure>
}
