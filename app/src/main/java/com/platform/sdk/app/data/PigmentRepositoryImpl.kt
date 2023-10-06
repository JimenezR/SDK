package com.platform.sdk.app.data

import com.platform.sdk.Failure
import com.platform.sdk.Result
import com.platform.sdk.app.data.dataSource.PigmentLocalDataSource
import com.platform.sdk.app.domain.PigmentRepository
import com.platform.sdk.app.domain.entity.Pigment
import javax.inject.Inject

internal class PigmentRepositoryImpl @Inject constructor(
    private val localDataSource: PigmentLocalDataSource
) : PigmentRepository {

    override suspend fun fetchPigments(): Result<List<Pigment>, Failure> =
        localDataSource.fetchPigments()
}
