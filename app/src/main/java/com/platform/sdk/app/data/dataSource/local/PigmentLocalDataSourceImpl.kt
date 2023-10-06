package com.platform.sdk.app.data.dataSource.local

import com.platform.sdk.Failure
import com.platform.sdk.Result
import com.platform.sdk.app.data.dataSource.PigmentLocalDataSource
import com.platform.sdk.app.domain.entity.Pigment

internal class PigmentLocalDataSourceImpl : PigmentLocalDataSource {

    override suspend fun fetchPigments(): Result<List<Pigment>, Failure> =
        Result.success(
            listOf(
                Pigment(
                    id = "123",
                    name = "Black",
                    value = 0L
                )
            )
        )
}
